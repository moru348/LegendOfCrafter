function initializeCoreMod() {
    var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
    var Opcodes = Java.type("org.objectweb.asm.Opcodes")
    var methodName = ASMAPI.mapMethod("runTick")
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList")

    return {
        "coremodmethod": {
            "target": {
                "type": "METHOD",
                "class": "net/minecraft/client/Minecraft",
                "methodName": methodName,
                "methodDesc": "()V"
            },
            "transformer": function(method) {
                var instructions = method.instructions
                var size = instructions.size()
                var list = new InsnList()
                for (var i = 0; i < size; i++) {
                    var value = instructions.get(i)
                    if(i >= 301 && i <= 307) {
                        list.add(null)
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