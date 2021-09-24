package dev.moru3.legendofcrafter.mixin

import net.minecraft.client.Minecraft
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject

@Mixin(Minecraft::class)
class EntityIgnoreTickrate {
    @Inject(method = ["runTick"], cancellable = true, at = [At("HEAD")])
    fun ig() {

    }
}