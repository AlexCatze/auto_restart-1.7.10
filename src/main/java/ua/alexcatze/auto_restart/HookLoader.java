package ua.alexcatze.auto_restart;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

@MCVersion(value = "1.7.10")
@SuppressWarnings("unused")
public class HookLoader implements IFMLLoadingPlugin, IClassTransformer {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {getClass().getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.command.server.CommandStop".equals(transformedName)) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cr.accept(
                    new ClassVisitor(Opcodes.ASM5, cw) {
                        @Override
                        public MethodVisitor visitMethod(
                                int access, String name, String desc, String signature, String[] exceptions) {
                            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                            if (name.equals("processCommand")) {
                                return new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitCode() {
                                        visitMethodInsn(
                                                Opcodes.INVOKESTATIC,
                                                "ua/alexcatze/auto_restart/util/ServerRestarter",
                                                "createStopFile",
                                                "()V",
                                                false);

                                        super.visitCode();
                                    }
                                };
                            }
                            return mv;
                        }
                    },
                    0);
            return cw.toByteArray();
        }
        return basicClass;
    }
}
