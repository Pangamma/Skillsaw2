package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.CsWrapper;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Taylor Love (Pangamma)
 */
public class ReviewCommand implements CommandExecutor {

	private final Main plugin;
	//private LinkedList<ReviewRequest> requests = new LinkedList<ReviewRequest>();
	private final String regexList = "^(?i)(l|list)(.*)";
	private final String regexTeleport = "^(?i)(tp|tele|goto)(.*)";
	private final String regexReviewNormal = "^(?i)(req|this|here|casual|normal)(.*)";
	private final LinkedList<ReviewRequest> requests = new LinkedList<>();

	public ReviewCommand(Main aThis){
		this.plugin = aThis;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args){
		if (!(cs instanceof Player)){
			return true;
		}
		Player p = (Player) cs;
		try{
			if (args[0].matches(regexReviewNormal)){
				if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_THIS)){
					return false;
				}
				//<editor-fold defaultstate="collapsed" desc="have they posted before in the last X minutes?">
				long timeOfLastPost = 0;
				long now = System.currentTimeMillis();
				ReviewRequest remove = null;
				boolean canPostAgain = true;
				for (ReviewRequest r : requests){
					if (r.getRequester().equalsIgnoreCase(p.getName())){
						canPostAgain = false;
						remove = r;
						//if the request is more than 1 hour old...
						if (r.getTimeOfReq() + (plugin.getConfigHandler().getMinutesBetweenReposts() * 60000) <= now){
							canPostAgain = true;
						}
					}
				}
				//</editor-fold>
				//<editor-fold defaultstate="collapsed" desc="either post it to the list, or tell them how long to wait. ">
				if (canPostAgain){
					if (remove != null){
						requests.remove(remove);
					}
					requests.addFirst(new ReviewRequest(p.getName(), p.getLocation()));
					cs.sendMessage("§2Post has been submitted to the top of the reviewing list. :)");
					BaseComponent[] bcPart1 = CText.legacy("§7" + p.getName() + " just created a new §aPeer§7 review request. Use ");
					BaseComponent[] bcCommand = CText.legacy("§f/review tp " + p.getName());
					CText.applyEvent(bcCommand, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to teleport")));
					CText.applyEvent(bcCommand, new ClickEvent(Action.RUN_COMMAND, "/review tp " + p.getName()));
					BaseComponent[] bcPart2 = CText.legacy("§7 if you want to see it. BTW, there is §aautocomplete§7 so you don't need to type in their full name.");

					bcPart1 = CText.merge(bcPart1, bcCommand);
					bcPart1 = CText.merge(bcPart1, bcPart2);

					for (Player pl : Bukkit.getOnlinePlayers()){
						if (pl != null && p.isValid()){
							pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1, 1);
							Player.Spigot sp = pl.spigot();
							if (sp != null){
								sp.sendMessage(bcPart1);
							}
						}
					}
				}
				else{
					double minutesRemaining = -1;
					if (remove != null){
						long msRemaining = (long) ((remove.getTimeOfReq() + plugin.getConfigHandler().getMinutesBetweenReposts() * 60000) - System.currentTimeMillis());
						minutesRemaining = (double) (msRemaining / 60000);
					}
					cs.sendMessage("§cYou are only allowed to post your builds once per §4" + plugin.getConfigHandler().getMinutesBetweenReposts()
							+ " minutes§c. D: You must wait §4" + minutesRemaining + " minutes§c before posting to the list again."
							+ " If your build §4falls off the end of the list§c you will be able to repost early.");
				}
				//</editor-fold>
				//<editor-fold defaultstate="collapsed" desc="purge so list stays under X reviews">
				while (requests.size() > plugin.getConfigHandler().getMaxEntriesToKeepInPublicReviewList()){
					requests.removeLast();
				}
				//</editor-fold>
			}
			else if (args[0].matches(regexList)){
				if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_LIST)){
					return false;
				}
				//<editor-fold defaultstate="collapsed" desc="list">
				cs.sendMessage(STATIC.C_DIV_LINE);
				ArrayList<BaseComponent[]> results = new ArrayList<BaseComponent[]>();
				for (ReviewRequest r : requests){
					String strike = r.visitors.contains(cs.getName()) ? "§m" : "";
					String msg = STATIC.C_MENU_CONTENT + strike + r.getRequester() + " @ " + ((System.currentTimeMillis() - r.getTimeOfReq()) / 3600000) + "h " + ((System.currentTimeMillis() - r.getTimeOfReq()) / 60000) + "m";
					BaseComponent[] text = CText.hoverText(msg, "Click to teleport");
					CText.applyEvent(text, new ClickEvent(Action.RUN_COMMAND, "/rev tp " + r.getRequester()));
					results.add(text);
				}
				CsWrapper cw = new CsWrapper(cs);
				while (!results.isEmpty()){
					cw.sendMessage(results.remove(results.size() - 1));
				}
				cs.sendMessage(STATIC.C_DIV_LINE);
				//<editor-fold defaultstate="collapsed" desc="summary">
				int size = requests.size();
				if (size == 0){
					cs.sendMessage(STATIC.C_MENU_CONTENT + "No one is waiting for a peer review right now.");
				}
				else if (size == 1){
					cs.sendMessage(STATIC.C_MENU_CONTENT + "There is one person waiting for a peer review right now.");
					cs.sendMessage(STATIC.C_MENU_CONTENT + "/review tp <their name>");
				}
				else{
					cs.sendMessage(STATIC.C_MENU_CONTENT + "There are " + requests.size() + " people waiting for a peer review right now.");
					cs.sendMessage(STATIC.C_MENU_CONTENT + "/review tp <their names>");
				}
				//</editor-fold>
				cs.sendMessage(STATIC.C_DIV_LINE);
				//</editor-fold>
			}
			else if (args[0].matches(regexTeleport)){
				if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_LIST)){
					return false;
				}
				//<editor-fold defaultstate="collapsed" desc="teleport">
				if (args.length == 1){
					if (requests.isEmpty()){
						cs.sendMessage("§cThere are no review requests right now.");
					}
					else{
						ReviewRequest match = null;
						for (ReviewRequest r : requests){
							if (!r.hasVisited(cs.getName())){
								match = r;
								break;
							}
						}
						if (match == null){
							cs.sendMessage("§cThere are no review requests right now. Go build!");
							match = requests.getFirst();
						}
						else{
							match.logReviewTeleportVisit(p);
						}
						p.teleport(match.getLoc());
					}
				}
				else if (args.length != 2){
					cs.sendMessage("§c/review tp [name of person]");
				}
				else{
					ReviewRequest failureModeMatch = null;
					ReviewRequest match = null;
					for (ReviewRequest r : requests){
						if (r.getRequester().equalsIgnoreCase(args[1])){
							match = r;
							break;
						}
						else if (r.getRequester().toLowerCase().contains(args[1].toLowerCase())){
							failureModeMatch = r;
						}
					}
					if (match != null){
						p.teleport(match.getLoc());
						match.logReviewTeleportVisit(p);
					}
					else if (failureModeMatch != null){
						p.teleport(failureModeMatch.getLoc());
						failureModeMatch.logReviewTeleportVisit(p);
					}
					else{
						cs.sendMessage("§cThere wasn't any review in the public review list under that name. Check to see which people are awaiting reviews. §4/review list");
					}
				}
				//</editor-fold>
			}
			else if (args[0].toLowerCase().equalsIgnoreCase("clear")){
				if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_REMOVE_OTHERS)){
					return false;
				}
				//<editor-fold defaultstate="collapsed" desc="clear">
				if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.ALL)){
					this.requests.clear();
					cs.sendMessage("§2Review list has been cleared.");
				}
				else{
					cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.ALL.node));
				}
				//</editor-fold>
			}
			else if (args[0].toLowerCase().equalsIgnoreCase("remove")){
				if (args.length == 2){
					if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_REMOVE_OTHERS)){
						return false;
					}
					if (this.requests.remove(new ReviewRequest(args[1], p.getLocation()))){
						cs.sendMessage("§2Review removed from the list.");
					}
					else{
						cs.sendMessage("§cCouldn't find a review with the poster's name: '" + args[1] + "'.");
					}
				}
				else{
					if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REVIEW_REMOVE_SELF)){
						return false;
					}
					this.requests.remove(new ReviewRequest(p.getName(), p.getLocation()));
					cs.sendMessage("§2Review removed from the list.");
				}
			}
			else{
				printHelp(cs);
			}
		}
		catch (ArrayIndexOutOfBoundsException aie){
			printHelp(cs);
		}

		return true;
	}

//<editor-fold defaultstate="collapsed" desc="review request class">
	private class ReviewRequest {

		private final String requester;
		private final Location loc;
		private final long timeOfReq;
		private final HashSet<String> visitors = new HashSet<String>();

		/**
		 *
		 * @param requester	-- who made the request?
		 * @param loc	-- the location the request was made in.
		 * @param isProReview	-- specifies whether or not review requests will
		 * be shown as red to all instructors.
		 */
		public ReviewRequest(String requester, Location loc){
			this.requester = requester;
			this.loc = loc;
			this.timeOfReq = System.currentTimeMillis();
			this.visitors.add(requester);
		}

		public String getRequester(){
			return requester;
		}

		public Location getLoc(){
			return loc;
		}

		public long getTimeOfReq(){
			return timeOfReq;
		}

		/**
		 * Returns this.requester *
		 */
		@Override
		public String toString(){
			return this.requester;
		}

		/**
		 * only compares the name of the requester, because there will only be
		 * one request per user at any given time. *
		 */
		@Override
		public boolean equals(Object o){
			if (o != null){
				if (o instanceof ReviewRequest){
					ReviewRequest req = (ReviewRequest) o;
					return this.requester.equalsIgnoreCase(req.requester);
				}
			}
			return false;
		}

		/**
		 * terrible method name, I know. Basically just use this method to log a
		 * teleport to this review request. *
		 */
		public void logReviewTeleportVisit(Player p){
			p.sendMessage("§aTeleported to " + getRequester() + "'s submission. Review it with the /rep command if you are feeling generous.");
			if (!this.visitors.contains(p.getName())){
				User visitor = plugin.getDataService().getUser(p.getName());
				if (visitor == null){
					// Let them teleport to the location again if they want the rep...
					// otherwise just let them do their visit, but don't try to give
					// a rep reward to a ghost.
//					p.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
					return;
				}
				this.visitors.add(p.getName());
				double tmp = visitor.getRepBetweenLevelAndLevel(visitor.getRepLevel(), visitor.getRepLevel() + 1);

				if (tmp < 0.1){
					tmp = 0.1;
				} //never give only zero.
				int level = visitor.getRepLevel();
				double bonusToGive = visitor.getRepBetweenLevelAndLevel(level, level + 1) / 200;
				double chance = (1.0 + new Random().nextInt(100)) / 100;
				bonusToGive *= chance;
				bonusToGive = User.round(bonusToGive);
				if (bonusToGive < 0.1){
					bonusToGive = 0.1;
				}
				else if (bonusToGive > 10){
					bonusToGive = 10;
				}
				visitor.addNaturalRep(bonusToGive);
				p.sendMessage("§2Randomly awarded " + bonusToGive + " rep for visiting this review request! :D");
				plugin.getDataService().saveUser(visitor);
			}
		}

		public boolean hasVisited(String name){
			return (this.visitors.contains(name));
		}
	}
//</editor-fold>

	public void printHelp(CommandSender cs){
		cs.sendMessage("§c/review this");
		cs.sendMessage("§c/review remove [name]");
		cs.sendMessage("§c/review l[ist]");
		cs.sendMessage("§c/review tp [name]");
		cs.sendMessage("§c/review clear");
	}
}
