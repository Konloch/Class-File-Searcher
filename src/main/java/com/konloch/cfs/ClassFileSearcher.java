package com.konloch.cfs;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Search for ClassFiles in arbitrary data
 *
 * @author Konloch
 * @since 7/20/2024
 */
public class ClassFileSearcher
{
	public static int PATH_LIMIT = 255;
	
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			System.out.println("Usage: cffs <command> <input files or directories>");
			System.out.println("  count: count the number of class files in the input files or directories");
			System.out.println("  classes: dump the class files in the input files or directories");
			System.out.println("  strings: dump the strings from inside of the class files in the input files or directories");
			System.exit(1);
		}
		
		String command = args[0];
		String[] inputs = Arrays.copyOfRange(args, 1, args.length);
		
		switch (command)
		{
			case "count":
				countClassFiles(inputs);
				break;
				
			case "classes":
				dumpClassFiles(inputs);
				break;
				
			case "strings":
				dumpStrings(inputs);
				break;
				
			default:
				System.out.println("Unknown command: " + command);
				System.exit(1);
		}
	}
	
	private static void countClassFiles(String[] inputs) throws IOException
	{
		int totalFound = 0;
		for (String input : inputs)
		{
			File file = new File(input);
			
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				if (files != null)
					for (File f : files)
						totalFound += extractClassFilesFromFile(f, false, false);
			}
			else
				totalFound += extractClassFilesFromFile(file, false, false);
		}
		
		System.out.println("Found " + totalFound + " valid classes");
	}
	
	private static void dumpClassFiles(String[] inputs) throws IOException
	{
		int totalFound = 0;
		for (String input : inputs)
		{
			File file = new File(input);
			
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				if (files != null)
					for (File f : files)
						totalFound += extractClassFilesFromFile(f, true, false);
			}
			else
				totalFound += extractClassFilesFromFile(file, true, false);
		}
		
		System.out.println("Found & extracted " + totalFound + " valid classes");
	}
	
	private static void dumpStrings(String[] inputs) throws IOException
	{
		int totalFound = 0;
		for (String input : inputs)
		{
			File file = new File(input);
			
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				if (files != null)
					for (File f : files)
						totalFound += extractClassFilesFromFile(f, false, true);
			}
			else
				totalFound += extractClassFilesFromFile(file, false, true);
		}
		
		System.out.println("Found & extracted strings from " + totalFound + " valid classes");
	}
	
	public static int extractClassFilesFromFile(File file, boolean dumpFoundFiles, boolean dumpFoundStrings) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int bytesRead;
		
		int classCount = 1;
		boolean inClass = false;
		ByteArrayOutputStream classBuffer = new ByteArrayOutputStream();
		
		while ((bytesRead = fis.read(buffer)) != -1)
		{
			for (int i = 0; i < bytesRead; i++)
			{
				if (buffer[i] == (byte) 0xCA && i + 3 < bytesRead)
				{
					if (buffer[i + 1] == (byte) 0xFE && buffer[i + 2] == (byte) 0xBA && buffer[i + 3] == (byte) 0xBE)
					{
						if (inClass)
						{
							//save the current class file
							byte[] classFileBytes = classBuffer.toByteArray();
							
							if (extractClassFileInformation(classFileBytes, true, dumpFoundFiles, dumpFoundStrings))
								classCount++;
							
							classBuffer.reset();
						}
						
						inClass = true;
					}
				}
				
				if (inClass)
					classBuffer.write(buffer[i]);
			}
		}
		
		//save the last class file
		if (inClass)
		{
			byte[] classFileBytes = classBuffer.toByteArray();
			if (extractClassFileInformation(classFileBytes, true, dumpFoundFiles, dumpFoundStrings))
				classCount++;
		}
		
		return classCount;
	}
	
	public static boolean extractClassFileInformation(byte[] classFileBytes, boolean verbose, boolean dumpFoundFiles, boolean dumpFoundStrings)
	{
		try
		{
			ReadClassFile classFile = verifyClassFile(classFileBytes);
			String packageName = classFile.classFile.substring(0, classFile.classFile.lastIndexOf('.')).replace('.', '/');
			String className = classFile.classFile.substring(classFile.classFile.lastIndexOf('.') + 1);
			
			StringBuilder checkedPackageName = new StringBuilder();
			
			//handle heavy obfuscation
			for (String s : packageName.split("/"))
			{
				if (s.length() >= PATH_LIMIT)
					s = className.substring(PATH_LIMIT - 30);
				
				if (checkedPackageName.length() > 0)
					checkedPackageName.append("/");
				
				checkedPackageName.append(s);
			}
			
			packageName = checkedPackageName.toString();
			
			//handle heavy obfuscation
			if (className.length() >= PATH_LIMIT)
				className = className.substring(PATH_LIMIT - 30);
			
			File parent = new File(packageName);
			parent.mkdirs();
			
			if (verbose)
				System.out.println("Found: " + classFile.classFile + (classFile.superClass.equals("java.lang.Object") ? "" : " extends " + classFile.superClass));
			
			//write to disk
			if (dumpFoundFiles)
			{
				File file = new File(parent, className + ".class");
				
				//rename file to something new to prevent collisions
				int counter = 0;
				while (file.exists())
				{
					String path = file.getAbsolutePath();
					file = new File(path.substring(0, path.length() - 6) + "-" + counter++ + ".class");
				}
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(classFileBytes);
				fos.close();
			}
			
			if (dumpFoundStrings)
			{
				File file = new File(parent, className + "-Strings.txt");
				
				//rename file to something new to prevent collisions
				int counter = 0;
				while (file.exists())
				{
					String path = file.getAbsolutePath();
					file = new File(path.substring(0, path.length() - 6) + "-" + counter++ + ".class");
				}
				
				FileOutputStream fos = new FileOutputStream(file);
				for (Object o : classFile.constantPool)
				{
					if (o == null)
						continue;
					
					fos.write(o.toString().getBytes(StandardCharsets.UTF_8));
					fos.write("\r\n".getBytes(StandardCharsets.UTF_8));
				}
				fos.close();
			}
			
			return true;
		}
		catch (Exception ignore)
		{
		
		}
		
		return false;
	}
	
	public static ReadClassFile verifyClassFile(byte[] classFileBytes) throws IOException
	{
		try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classFileBytes)))
		{
			//classfile magic number
			int magic = dis.readInt();
			if (magic != 0xCAFEBABE)
				throw new IOException("Invalid class file");
			
			//classfile header
			dis.readUnsignedShort();
			dis.readUnsignedShort();
			
			//classfile constant pool count
			int constantPoolCount = dis.readUnsignedShort();
			Object[] constantPool = new Object[constantPoolCount];
			HashSet<Integer> constantIndex = new HashSet<>();
			
			//read the constant pool
			for (int i = 1; i < constantPoolCount; i++)
			{
				int tag = dis.readUnsignedByte();
				switch (tag)
				{
					case 7: //CONSTANT_Class
						constantIndex.add(i);
						constantPool[i] = dis.readUnsignedShort(); //name index
						break;
					case 1: //CONSTANT_Utf8
						constantPool[i] = dis.readUTF();
						break;
					case 5: //CONSTANT_Long
					case 6: //CONSTANT_Double
						dis.readLong(); //skip 8 bytes
						i++; //these take up two entries in the constant pool
						break;
					case 3: //CONSTANT_Integer
					case 4: //CONSTANT_Float
						dis.readInt(); //skip 4 bytes
						break;
					case 8: //CONSTANT_String
					case 16: //CONSTANT_MethodType
						dis.readUnsignedShort(); //skip index
						break;
					case 9:  //CONSTANT_Fieldref
					case 10: //CONSTANT_Methodref
					case 11: //CONSTANT_InterfaceMethodref
					case 12: //CONSTANT_NameAndType
					case 18: //CONSTANT_InvokeDynamic
						dis.readUnsignedShort(); //skip two indexes
						dis.readUnsignedShort();
						break;
					case 15: //CONSTANT_MethodHandle
						dis.readUnsignedByte(); //skip reference kind
						dis.readUnsignedShort(); //skip reference index
						break;
					default:
						throw new IOException("Unexpected constant pool tag: " + tag);
				}
			}
			
			//read access flags, this class index, and super class index
			int accessFlags = dis.readUnsignedShort();
			int thisClassIndex = dis.readUnsignedShort();
			int superClassIndex = dis.readUnsignedShort();
			
			//obtain classfile name
			int nameIndex = (int) constantPool[thisClassIndex];
			int superNameIndex = (int) constantPool[superClassIndex];
			String className = ((String) constantPool[nameIndex]).replace('/', '.');
			String superClassName = ((String) constantPool[superNameIndex]).replace('/', '.');
			
			//clean constant pool so it's just UTF-8 strings
			for (int i : constantIndex)
				constantPool[i] = null;
			
			//return the read class file
			return new ReadClassFile(className, superClassName, constantPool);
		}
	}
}