package de.androbin.xml;

public final class Parser
{
	private Parser()
	{
	}
	
	public static Node parseXML( final String xml )
	{
		final State state = new State( xml );
		
		for ( state.charIndex = 0; state.charIndex < state.chars.length; state.charIndex++ )
		{
			final char c = state.chars[ state.charIndex ];
			
			if ( System.lineSeparator().contains( String.valueOf( c ) ) )
			{
				continue;
			}
			
			switch ( state.pointer )
			{
				case HEAD_TYPE :
				{
					parseHeadType( state, c );
					break;
				}
				
				case HEAD_SPACE :
				{
					parseHeadSpace( state, c );
					break;
				}
				
				case HEAD_KEY :
				{
					parseHeadKey( state, c );
					break;
				}
				
				case HEAD_VALUE :
				{
					parseHeadValue( state, c );
					break;
				}
				
				case BODY_CONTENT :
				{
					if ( parseBodyContent( state, c ) )
					{
						return state.nodes.peekFirst();
					}
					
					break;
				}
			}
		}
		
		return null;
	}
	
	private static boolean parseBodyContent( final State state, final char c )
	{
		if ( c == '<' )
		{
			if ( state.charIndex + 1 < state.chars.length )
			{
				switch ( state.chars[ state.charIndex + 1 ] )
				{
					case '!' :
					case '?' :
					{
						state.meta = true;
						state.charIndex++;
					}
					
					default :
					{
						state.pointer = Pointer.HEAD_TYPE;
						state.builder.setLength( 0 );
						break;
					}
					
					case '/' :
					{
						return parseBodyContentClose( state, c );
					}
				}
			}
		}
		else
		{
			state.builder.append( c );
		}
		
		return false;
	}
	
	private static boolean parseBodyContentClose( final State state, final char c )
	{
		boolean error = false;
		
		final Node node = state.nodes.peekFirst();
		final String type = node.type;
		final int type_length = type.length();
		
		{
			final int endex = state.charIndex + type_length + 2;
			
			if ( endex >= state.chars.length || state.chars[ endex ] != '>' )
			{
				error = true;
			}
			
			for ( int typeIndex = 0; typeIndex < type_length && !error; typeIndex++ )
			{
				final int charIndex2 = state.charIndex + typeIndex + 2;
				
				if ( charIndex2 >= state.chars.length || state.chars[ charIndex2 ] != type.charAt( typeIndex ) )
				{
					error = true;
				}
			}
		}
		
		if ( error )
		{
			state.builder.append( c );
		}
		else
		{
			{
				final String content = state.builder.toString();
				node.content = content;
				state.nodes.removeFirst();
			}
			
			if ( state.nodes.peekFirst().parent == null )
			{
				return true;
			}
			else
			{
				state.charIndex += type_length + 2;
				state.builder.setLength( 0 );
			}
		}
		
		return false;
	}
	
	private static void parseHeadKey( final State state, final char c )
	{
		switch ( c )
		{
			default :
			{
				state.builder.append( c );
				break;
			}
			
			case '=' :
			{
				state.key = state.builder.toString();
				break;
			}
			
			case '\"' :
			{
				state.pointer = Pointer.HEAD_VALUE;
				state.builder.setLength( 0 );
				break;
			}
		}
	}
	
	private static void parseHeadSpace( final State state, final char c )
	{
		switch ( c )
		{
			default :
			{
				state.pointer = Pointer.HEAD_KEY;
				state.builder.setLength( 0 );
				state.builder.append( c );
				break;
			}
			
			case ' ' :
			{
				break;
			}
			
			case '>' :
			{
				state.pointer = Pointer.BODY_CONTENT;
				state.builder.setLength( 0 );
				break;
			}
		}
	}
	
	private static void parseHeadType( final State state, final char c )
	{
		if ( state.meta )
		{
			/**/ if ( c == '\"' )
			{
				state.quote ^= true;
			}
			else if ( c == '>' && !state.quote )
			{
				state.meta = false;
				state.pointer = Pointer.BODY_CONTENT;
			}
		}
		else
		{
			switch ( c )
			{
				default :
				{
					state.builder.append( c );
					break;
				}
				
				case '>' :
				{
					state.charIndex--;
				}
				
				case ' ' :
				{
					final Node parent = state.nodes.peekFirst();
					final String type = state.builder.toString();
					final Node node = new Node( parent, type );
					parent.addChild( node );
					state.nodes.addFirst( node );
					
					state.pointer = Pointer.HEAD_SPACE;
					break;
				}
			}
		}
		
	}
	
	private static void parseHeadValue( final State state, final char c )
	{
		if ( c == '\"' )
		{
			final String value = state.builder.toString();
			final Node node = state.nodes.peekFirst();
			node.addMetadata( state.key, value );
			state.pointer = Pointer.HEAD_SPACE;
		}
		else
		{
			state.builder.append( c );
		}
	}
	
	public static enum Pointer
	{
		HEAD_TYPE,
		HEAD_SPACE,
		HEAD_KEY,
		HEAD_VALUE,
		BODY_CONTENT;
	}
}