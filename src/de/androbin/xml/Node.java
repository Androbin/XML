package de.androbin.xml;

import java.util.*;

public final class Node
{
	public static final int				INDENTATION	= 4;
	
	public final Node					parent;
	public final String					type;
	private final Map<String, String>	metadata	= new HashMap<String, String>();
	public String						content;
	private final Set<Node>				children	= new HashSet<Node>();
	
	public Node()
	{
		this( "root" );
	}
	
	public Node( final String type )
	{
		this.parent = null;
		this.type = type;
	}
	
	public Node( final Node parent, final String type ) throws IllegalArgumentException
	{
		if ( type == null || type.isEmpty() )
		{
			throw new IllegalArgumentException( "Type cannot be null nor empty" );
		}
		
		this.parent = parent;
		this.type = type;
	}
	
	public boolean addChild( final Node child ) throws IllegalArgumentException
	{
		if ( child == null )
		{
			throw new IllegalArgumentException( "Child cannot be null" );
		}
		
		if ( hasChild( child ) )
		{
			return false;
		}
		else
		{
			children.add( child );
			return true;
		}
	}
	
	public boolean addMetadata( final String key, final String value ) throws IllegalArgumentException
	{
		if ( key == null || key.isEmpty() )
		{
			throw new IllegalArgumentException( "Key cannot be null nor empty" );
		}
		
		if ( value == null )
		{
			return addMetadata( key, "" );
		}
		
		if ( hasMetadata( key ) )
		{
			return false;
		}
		else
		{
			metadata.put( key, value );
			return true;
		}
	}
	
	public Set<Node> getChildrenByType( final String type )
	{
		final Set<Node> children = new HashSet<Node>();
		
		for ( final Node child : children )
		{
			if ( child.type.equals( type ) )
			{
				children.add( child );
			}
		}
		
		return children;
	}
	
	public String getMetadata( final String key )
	{
		return metadata.get( key );
	}
	
	public boolean hasChild( final Node node )
	{
		return children.contains( node );
	}
	
	public boolean hasContent()
	{
		return content != null && !content.isEmpty();
	}
	
	public boolean hasMetadata( final String key )
	{
		return metadata.containsKey( key );
	}
	
	public boolean removeChild( final Node child )
	{
		if ( hasChild( child ) )
		{
			children.remove( child );
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean removeMetadata( final String key )
	{
		if ( hasMetadata( key ) )
		{
			metadata.remove( key );
			return true;
		}
		else
		{
			return false;
		}
	}
}
