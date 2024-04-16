package com.custom.plugin

import com.custom.plugin.config.RunTimeConfigMgr
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.LocalVariablesSorter


class RunTimeMethodVisitor(
    access: Int,
    descriptor: String?,
    mv: MethodVisitor,
    private val clazzName: String?,
    private val methodName: String?
) : LocalVariablesSorter(Opcodes.ASM9, access, descriptor, mv) {

    private var isAnnotated = false // 是否可以插桩
    private var time = 0

    /**
     * 最先执行 判断是否可以插桩
     * 开启允许全部或者有注解标记
     */
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        isAnnotated = ("Lcom/custom/pluginconfig/TimeConsume;" == descriptor)
        return super.visitAnnotation(descriptor, visible)
    }

    private fun isInsertEnable(): Boolean {
        return if (RunTimeConfigMgr.runTimePluginConfig?.applyToAll == true) {
            true
        } else {
            isAnnotated
        }
    }

    /**
     * 遍历代码的开始 在头部插入当前时间
     */
    override fun visitCode() {
        super.visitCode()
        if (isInsertEnable()) {
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "currentTimeMillis",
                "()J",
                false
            )
            time = newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(Opcodes.LSTORE, time)
        }
    }

    /**
     * 遍历操作码 判断是否是return语句 在return之前计算方法耗时
     * @param opcode 操作码
     */
    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN || opcode === Opcodes.ATHROW) {
            if (isInsertEnable()) {
                val l1 = Label()
                mv.visitLabel(l1)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "java/lang/System",
                    "currentTimeMillis",
                    "()J",
                    false
                )
                mv.visitVarInsn(Opcodes.LLOAD, time)
                mv.visitInsn(Opcodes.LSUB)
                mv.visitVarInsn(Opcodes.LSTORE, 3)
                val l2 = Label()
                mv.visitLabel(l2)
                mv.visitLdcInsn("RunTimePlugin")
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/StringBuilder",
                    "<init>",
                    "()V",
                    false
                )
                mv.visitLdcInsn("$clazzName/$methodName 耗时：")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                    false
                )
                mv.visitVarInsn(Opcodes.LLOAD, 3)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(J)Ljava/lang/StringBuilder;",
                    false
                )
                mv.visitLdcInsn("ms.")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "toString",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "android/util/Log",
                    "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I",
                    false
                )
            }
        }
        super.visitInsn(opcode)
    }
}