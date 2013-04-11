package hx.spectator;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import hx.utils.HyperMod;
import hx.utils.PacketHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

@Mod(modid = "mod_Spectator", name = "Spectator", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
channels={"file_header", "file_request", "file_segment"}, packetHandler = PacketHandlerSpectator.class)
public class ModSpectator{

	@ServerStarting
	   public void serverStarting(FMLServerStartingEvent event) {
	      CommandHandler commandManager = (CommandHandler)event.getServer().getCommandManager();
	      commandManager.registerCommand(new CommandSpectate());
	   }
}
