package hx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IClassTransformer;

public abstract class MethodInjectorTransformer implements IClassTransformer
{
    //injected class
    //injected method
    //reported class
    //reported method
    //field param passing
    //set cancel
    //put obf

    private Method injected, reported;
    private ArrayList<String> fields = new ArrayList<String>();
    private TreeMap<Integer, Class> params  = new TreeMap<Integer, Class>();

    private void init()
    {
        URLClassLoader source = (URLClassLoader) getClass().getClassLoader();
        URLClassLoader ucl = new URLClassLoader(source.getURLs());

        try
        {
            try
            {
                ucl.loadClass("net.minecraft.world.Explosion");
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Environment Obfuscated.");

                for (Field f : this.getClass().getFields())
                {
                    Obfuscated anno = f.getAnnotation(Obfuscated.class);

                    if (anno != null)
                    {
                        f.set(this, anno.value());
                    }
                }
            }
            finally
            {
                System.err.println("Attempting inject...");
                this.setup(ucl);
                System.err.println("Method injection successful.");
            }
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("inject failed");
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            System.err.println("cant find method to inject");
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            System.err.println("not safe to inject");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected abstract void setup(URLClassLoader ucl) throws Exception;

    protected void reportField(String name)
    {
        fields.add(name);
    }

    protected void reportParam(int i, Class type)
    {
        params.put(i, type);
    }

    protected void inject(Method injected, Method reported)
    {
        this.injected = injected;
        this.reported = reported;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (name.equals("hx.utils.Obfuscated"))
        {
            return bytes;
        }

        if (injected == null)
        {
            init();
        }

        if (!name.equals(injected.getDeclaringClass().getName()))
        {
            return bytes;
        }

        System.err.println("found class.");
        ClassReader cr = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (Object methodNode : classNode.methods)
        {
            MethodNode method = (MethodNode)methodNode;

            if (method.name.equals(injected.getName()) &&
                    method.desc.equals(Type.getMethodDescriptor(injected)))
            {
                System.err.println("found method.");
                InsnList list = new InsnList();
                //reported class
                list.add(new FieldInsnNode(Opcodes.GETSTATIC,
                        Type.getInternalName(reported.getDeclaringClass()),
                        "instance",
                        Type.getDescriptor(reported.getDeclaringClass())
                                          ));

                //push obj
                if (!Modifier.isStatic(injected.getModifiers()))
                {
                    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                }

                //for each field, push it
                for (String fieldName: fields)
                {
                    try
                    {
                        Field f = injected.getDeclaringClass().getDeclaredField(fieldName);
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new FieldInsnNode(Opcodes.GETFIELD,
                                Type.getInternalName(injected.getDeclaringClass()),
                                fieldName,
                                Type.getDescriptor(f.getType())));
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                for (int i: params.keySet())
                {
                    Class type = params.get(i);

                    if (type.equals(boolean.class) || type.equals(int.class) || type.equals(byte.class) || type.equals(short.class) || type.equals(char.class))
                    {
                        list.add(new VarInsnNode(Opcodes.ILOAD, i));
                    }
                    else if (type.equals(long.class))
                    {
                        list.add(new VarInsnNode(Opcodes.LLOAD, i));
                    }
                    else if (type.equals(float.class))
                    {
                        list.add(new VarInsnNode(Opcodes.FLOAD, i));
                    }
                    else if (type.equals(double.class))
                    {
                        list.add(new VarInsnNode(Opcodes.DLOAD, i));
                    }
                    else
                    {
                        list.add(new VarInsnNode(Opcodes.ALOAD, i));
                    }
                }

                list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        Type.getInternalName(reported.getDeclaringClass()),
                        reported.getName(),
                        Type.getMethodDescriptor(reported)));

                if (!reported.getReturnType().equals(void.class))
                {
                    Class type = reported.getReturnType();

                    if (type.equals(boolean.class) || type.equals(int.class) || type.equals(byte.class) || type.equals(short.class) || type.equals(char.class))
                    {
                        list.add(new InsnNode(Opcodes.IRETURN));
                    }
                    else if (type.equals(long.class))
                    {
                        list.add(new InsnNode(Opcodes.LRETURN));
                    }
                    else if (type.equals(float.class))
                    {
                        list.add(new InsnNode(Opcodes.FRETURN));
                    }
                    else if (type.equals(double.class))
                    {
                        list.add(new InsnNode(Opcodes.DRETURN));
                    }
                    else
                    {
                        list.add(new InsnNode(Opcodes.ARETURN));
                    }
                }

                method.instructions.insert(list);
                method.maxStack = Math.max(method.maxStack, params.size() + fields.size() + 1);
            }
        }

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
