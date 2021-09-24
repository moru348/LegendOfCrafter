function initializeCoreMod() {
    var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var LdcInsnNode = Java.type("org.objectweb.asm.tree.LdcInsnNode");
    var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
    var Opcodes = Java.type("org.objectweb.asm.Opcodes")
    var methodName = ASMAPI.mapMethod("func_240802_v_")
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList")

    return {
        "coremodmethod": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.server.MinecraftServer",
                "methodName": methodName,
                "methodDesc": "()V"
            },
            "transformer": function(method) {
                var instructions = method.instructions
                var size = instructions.size()
                var list = new InsnList()
                for (var i = 0; i < size; i++) {
                    var value = instructions.get(i)
                    if(value instanceof LdcInsnNode && value.cst.toString() === "50") {
                        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "dev/moru3/legendofcrafter/Data", "NOW_SERVER_TICK_RATE", "J"))
                    } else {
                        list.add(value)
                    }
                }
                method.instructions.clear()
                method.instructions.add(list)
                return method
            }
        }
    }
}