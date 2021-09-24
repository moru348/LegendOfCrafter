package dev.moru3.legendofcrafter

import dev.moru3.legendofcrafter.LegendOfCrafter.Sounds.HIT
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.vector.Vector3i
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.event.entity.player.ArrowNockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.max
import kotlin.math.min

@Mod("legend_of_crafter")
class LegendOfCrafter {

    private val usingPlayers = mutableMapOf<PlayerEntity, Timer>()
    private var serverTick: Long = 50
    private var tickCount: Long = 0

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onArrowNock(event: ArrowNockEvent) {
        if (usingPlayers.containsKey(event.player) || event.player.isOnGround || !event.hasAmmo() || event.player.world.getBlockState(event.player.position.subtract(Vector3i(0, 1, 0))).material != Material.AIR || event.player.world.getBlockState(event.player.position.subtract(Vector3i(0, 2, 0))).material != Material.AIR || event.player.motion.y > 0.1) { return }
        val startVecY = event.player.motion.y
        usingPlayers[event.player] = Timer().also { timer ->
            timer.scheduleAtFixedRate(0, 10) {
                if (!event.player.heldItemMainhand.item.name.string.lowercase().contains("bow") || event.player.foodStats.foodLevel <= 1 || event.player.isOnGround) {
                    setTickRate(50)
                    usingPlayers.remove(event.player)
                    event.player.fallDistance = 0F
                    event.player.setVelocity(event.player.motion.x, max(startVecY, -0.6), event.player.motion.z)
                    timer.cancel()
                    this.cancel()
                } else {
                    setTickRate(min(800, serverTick + 10))
                    if (tickCount % 10 == 0L) {
                        LivingEntity::class.java.getDeclaredMethod("updateActiveHand").also { it.isAccessible = true }.invoke(event.player)
                        LivingEntity::class.java.getDeclaredMethod("updateArmSwingProgress").also { it.isAccessible = true }.invoke(event.player)
                        event.player.cooldownTracker.tick()
                    }
                    event.player.setVelocity(event.player.motion.x, max(event.player.motion.y, -0.6), event.player.motion.z)
                    tickCount++
                }
                Data.BOW_USE_DURATION = 10
            }
        }
        setTickRate(200)
        event.world.playSound(event.entity.posX, event.entity.posY, event.entity.posZ, HIT, SoundCategory.MASTER, 1F, 1F, false)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onFovUpdate(event: FOVUpdateEvent) {
        if(usingPlayers.containsKey(event.entity)) {
            event.newfov = 1F+(serverTick/1800F)
        }
    }

    private fun setTickRate(rate: Long) {
        serverTick = rate
        val timer = Minecraft::class.java.getDeclaredField("timer").also { it.isAccessible = true }.get(Minecraft.getInstance()) as net.minecraft.util.Timer
        timer::class.java.getDeclaredField("tickLength").also { it.isAccessible = true }.set(timer, rate)
        Data.NOW_SERVER_TICK_RATE = rate
    }

    object Sounds {
        val HIT = ResourceLocation("legend_of_crafter", "hit").run { SoundEvent(this).also { it.registryName = this } }
    }

    @SubscribeEvent
    fun onSoundRegistry(event: Register<SoundEvent>) {
        event.registry.registerAll(HIT)
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }
}

object Data {
    @JvmField
    var NOW_SERVER_TICK_RATE = 50L
    @JvmField
    var BOW_USE_DURATION = 72000
}