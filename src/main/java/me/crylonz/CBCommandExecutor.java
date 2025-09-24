package me.crylonz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crylonz.CubeBall.*;

public class CBCommandExecutor implements CommandExecutor {

    public CBCommandExecutor() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player player;
        if ((sender instanceof Player)) {
            player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("cb")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("match") && player.hasPermission("cubeball.manage")) {
                        balls.remove(BALL_MATCH_ID);
                        match = new Match();
                        player.sendMessage(I18n.get("new_match_created"));
                    } else if (args[0].equalsIgnoreCase("scanpoint") && player.hasPermission("cubeball.manage")) {
                        if (match != null) match.scanPoint(player);
                    } else if (args[0].equalsIgnoreCase("scanplayer") && player.hasPermission("cubeball.manage")) {
                        if (match != null) {
                            match.scanPlayer();
                            match.displayTeams(player);
                        }
                    } else if (args[0].equalsIgnoreCase("start") && player.hasPermission("cubeball.manage")) {
                        if (match != null) {
                            match.start(player);
                        } else {
                            player.sendMessage(I18n.get("need_create_match"));
                        }
                    } else if (args[0].equalsIgnoreCase("stop") && player.hasPermission("cubeball.manage")) {
                        if (match != null) {
                            Ball ball = balls.remove(BALL_MATCH_ID);
                            if (ball != null && ball.getBall() != null) ball.getBall().remove();
                            match.reset();
                            player.sendMessage(I18n.get("match_cancelled"));
                        } else {
                            player.sendMessage(I18n.get("no_match_to_stop"));
                        }
                    } else if (args[0].equalsIgnoreCase("pause") && player.hasPermission("cubeball.manage")) {
                        if (match != null && match.pause()) {
                            for (Player p : match.getAllPlayer(true)) {
                                if (p != null) {
                                    p.sendMessage(I18n.format("match_paused_by", "name", player.getName()));
                                }
                            }
                        } else {
                            player.sendMessage(I18n.get("no_match_to_pause"));
                        }
                    } else if (args[0].equalsIgnoreCase("resume") && player.hasPermission("cubeball.manage")) {
                        if (match != null && match.resume()) {
                            for (Player p : match.getAllPlayer(true)) {
                                if (p != null) {
                                    p.sendMessage(I18n.format("match_resumed_by", "name", player.getName()));
                                }
                            }
                        } else {
                            player.sendMessage(I18n.get("no_match_to_resume"));
                        }
                    } else {
                        player.sendMessage(I18n.get("unknown_command"));
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("generate") && player.hasPermission("cubeball.manage")) {
                        if (balls.get(args[1]) == null) {
                            generateBall(args[1], player.getLocation(), null);
                            player.sendMessage(I18n.format("ball_generated", "id", args[1]));
                        } else {
                            player.sendMessage(I18n.format("ball_exists", "id", args[1]));
                        }
                    } else if (args[0].equalsIgnoreCase("remove") && player.hasPermission("cubeball.manage")) {
                        if (balls.get(args[1]) != null) {
                            destroyBall(args[1]);
                            player.sendMessage(I18n.format("ball_removed", "id", args[1]));
                        } else {
                            sender.sendMessage(I18n.format("ball_not_exists", "id", args[1]));
                        }

                    }
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("team") && player.hasPermission("cubeball.manage")) {
                        if (match != null) {
                            try {
                                Team team = Team.valueOf(args[1].toUpperCase());
                                Player playerToAdd = Bukkit.getPlayer(args[2]);
                                if (match.addPlayerToTeam(playerToAdd, team)) {
                                    player.sendMessage(I18n.format("player_added_to_team", "team", args[1].toUpperCase()));
                                    match.displayTeams(player);
                                    player.sendMessage(I18n.get("use_cb_start"));
                                } else {
                                    player.sendMessage(I18n.get("cannot_find_player"));
                                }
                            } catch (IllegalArgumentException | NullPointerException e) {
                                player.sendMessage(I18n.get("team_must_be"));
                            }
                        } else {
                            player.sendMessage(I18n.get("need_create_match"));
                        }
                    }
                }
                if (args.length == 6) {
                    if (args[0].equalsIgnoreCase("generate") && player.hasPermission("cubeball.manage")) {
                        generateWithPosition(sender, args);
                    }
                }
            }

        } else {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (balls.get(args[1]) != null) {
                        destroyBall(args[1]);
                        sender.sendMessage(I18n.format("ball_removed", "id", args[1]));
                    } else {
                        sender.sendMessage(I18n.format("ball_not_exists", "id", args[1]));
                    }
                }

            }

            if (args.length == 6) {
                if (args[0].equalsIgnoreCase("generate")) {
                    generateWithPosition(sender, args);
                }
            }
        }
        return true;
    }

    private static void generateWithPosition(CommandSender sender, String[] args) {
        if (balls.get(args[1]) == null) {
            if (Bukkit.getWorld(args[5]) != null) {
                generateBall(args[1],
                        new Location(Bukkit.getWorld(args[5]),
                                Double.parseDouble(args[2]),
                                Double.parseDouble(args[3]),
                                Double.parseDouble(args[4])
                        ), null);
                sender.sendMessage(I18n.format("ball_generated_at", "id", args[1], "x", args[2], "y", args[3], "z", args[4], "world", args[5]));
            } else {
                sender.sendMessage(I18n.format("unknown_world", "world", args[5]));
            }
        } else {
            sender.sendMessage(I18n.format("ball_exists", "id", args[1]));
        }
    }
}
