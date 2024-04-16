package com.custom.plugin

import com.custom.plugin.config.RunTimeConfigMgr
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_INTERFACE


class RunTimeClassVisitor(private val clazzName: String, classVisitor: ClassVisitor?) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    private var isInterface = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        cv.visit(version, access, name, signature, superName, interfaces)
        isInterface = (access and ACC_INTERFACE) !== 0
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        val clazzValid = RunTimeConfigMgr.runTimePluginConfig != null &&
                RunTimeConfigMgr.runTimePluginConfig!!.applyPackageName.isNotEmpty() &&
                clazzName.contains(RunTimeConfigMgr.runTimePluginConfig!!.applyPackageName)

        return if (!isInterface && mv != null && !name.equals("<init>") && clazzValid) {
            println("插桩class：$clazzName || $name")
            RunTimeMethodVisitor(access, descriptor, mv, clazzName, name)
        } else {
            mv
        }
    }

}