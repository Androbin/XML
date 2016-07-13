package de.androbin.xml;

import de.androbin.xml.Parser.*;
import java.util.*;

public final class State
{
	public final char[]		 chars;
							 
	public final Deque<Node> nodes;
	public StringBuilder	 builder;
							 
	public Pointer			 pointer;
	public String			 key;
							 
	public boolean			 meta;
	public boolean			 quote;
							 
	public int				 charIndex;
							 
	public State( final String xml )
	{
		chars = xml.toCharArray();
		nodes = new LinkedList<Node>();
		nodes.addFirst( new Node() );
		builder = new StringBuilder();
		pointer = Pointer.BODY_CONTENT;
	}
}