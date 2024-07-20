package com.konloch.cfs;

/**
 * @author Konloch
 * @since 7/20/2024
 */
public class ReadClassFile
{
	public final String classFile;
	public final String superClass;
	public final Object[] constantPool;
	
	public ReadClassFile(String classFileName, String superClass, Object[] constantPool)
	{
		this.classFile = classFileName;
		this.superClass = superClass;
		this.constantPool = constantPool;
	}
}
