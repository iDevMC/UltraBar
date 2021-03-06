package me.ryandw11.ultrabar;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import me.ryandw11.ultrabar.core.UltraBar;


public class BossBarSced extends BukkitRunnable{
	private int num;
	private int currentNum;
	private int time;
	private int lastNum;

	
	private UltraBar b;
	
	private BossBar bar;
	private Double progress;

	public BossBarSced(){
		this.b = UltraBar.plugin;
		num = b.getConfig().getInt("BossBarMessages.Number_Of_Messages");
		currentNum = 1;
		lastNum = 1;
	}
	
	public void startProgram(){
		if(num <= 0){
			b.getLogger().warning("BossBarMessages.Number_Of_Messages is set to 0! Please change enabled to false or add a message!");
			return;
		}
		BossBar bar = Bukkit.createBossBar(
				b.chatColorUtil.translateChatColor( b.getConfig().getString("BossBarMessages." + currentNum + ".Message")),
				GrabBarStyles.barColor(b.getConfig().getString("BossBarMessages." + currentNum + ".Color")), 
				GrabBarStyles.barStyle(b.getConfig().getString("BossBarMessages." + currentNum + ".Style")
						));
		UltraBar.barMessage = bar;
		this.bar = bar;
		double ticks = 1;
		time = b.getConfig().getInt("BossBarMessages." + currentNum + ".Time") * 20; //Multiply by 20 to convert seconds to ticks.
		this.progress = ticks / time;
		bar.setProgress(1);
		
		for(Player p : Bukkit.getOnlinePlayers()){ //adds all players online to the bar
			bar.addPlayer(p);
		}
		
		//long delay = b.getConfig().getInt("BossBarMessages.Delay"); //Default of 5
		
		this.runTaskTimer(b, 5L, 1L);
	}
	
	@Override
	public void run() {
		
		
		if(currentNum > num){
			currentNum = 1;
		}
		
		double prog = bar.getProgress() - progress;
        if(prog < 0){
        	/*
        	 * What happens if the bar is done.
        	 * It resets!
        	 */
        	lastNum = currentNum;
        	currentNum += 1;
        	if(currentNum > num){
    			currentNum = 1;
    		}
        	
        	if(b.getConfig().getBoolean("BossBarMessages.Random_Order")){
        		Random rand = new Random();
        		int n = lastNum;
        		while(n == lastNum){
        			n = rand.nextInt(b.getConfig().getInt("BossBarMessages.Number_Of_Messages")) + 1;
        		}
        		currentNum = n;
        	}
        	
        	for(Player p : bar.getPlayers()){
        		if(!b.getConfig().getBoolean("BossBarMessages.World_Whitelist_Enabled")){
        			if(!p.isOnline() || b.getToggledPlayers().contains(p)){
        				bar.removePlayer(p);
        			}
        		}else{
        			if(!p.isOnline() || !b.getConfig().getList("BossBarMessages.World_Whitelist").contains(p.getWorld().getName()) || b.getToggledPlayers().contains(p)){
        				bar.removePlayer(p);
        			}
        		}
        	}
        	
        	if(!b.getConfig().contains("BossBarMessages." + currentNum) 
        			|| GrabBarStyles.barColor(b.getConfig().getString("BossBarMessages." + currentNum + ".Color")) == null){
        		b.getLogger().severe("There is an error with the configuration!");
        		b.getLogger().severe("There are not " + currentNum + " boss bar messages!");
        		b.getLogger().severe("Please fix the errors! The boss bar announce has been disabled.");
        		b.getLogger().severe("For more assistance please contact me on github or spigot.");
        		UltraBar.barMessage = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_10);
        		bar.setVisible(false);
        		this.cancel();
        	}
        	bar.setColor(GrabBarStyles.barColor(b.getConfig().getString("BossBarMessages." + currentNum + ".Color")));
        	bar.setStyle(GrabBarStyles.barStyle(b.getConfig().getString("BossBarMessages." + currentNum + ".Style")));
        	bar.setTitle(b.chatColorUtil.translateChatColor(b.papi.getMessage(b.getConfig().getString("BossBarMessages." + currentNum + ".Message"), null)));
        	bar.setProgress(1);
        	double ticks = 1;
        	time = b.getConfig().getInt("BossBarMessages." + currentNum + ".Time") * 20;
    		this.progress = ticks / time;
    		
    		for(Player p : Bukkit.getOnlinePlayers()){
    			if(b.getConfig().getBoolean("BossBarMessages.World_Whitelist_Enabled")){ //Bug Fix in 1.6.1 | Capitalization error.
    				if(b.getConfig().getString("BossBarMessages.World_Whitelist").contains(p.getWorld().getName())){
    					if(!bar.getPlayers().contains(p) && !b.getToggledPlayers().contains(p))
    						bar.addPlayer(p);
    				}
    			}
    			else{
    				if(!bar.getPlayers().contains(p) && !b.getToggledPlayers().contains(p))
						bar.addPlayer(p);
    			}
    		}
    		
    		
        } //End of if the bar needs to be reset.
        else{
        	bar.setProgress(prog);
        }
        
        bar.setTitle(ChatColor.translateAlternateColorCodes('&', b.papi.getMessage(b.getConfig().getString("BossBarMessages." + currentNum + ".Message"), null)));
		
		
	}
	

}
