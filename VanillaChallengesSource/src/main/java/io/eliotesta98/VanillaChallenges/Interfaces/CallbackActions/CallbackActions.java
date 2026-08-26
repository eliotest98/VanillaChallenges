package io.eliotesta98.VanillaChallenges.Interfaces.CallbackActions;

import com.HeroxWar.HeroxCore.MessageGesture.MessageGesturePaper;
import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import com.HeroxWar.HeroxCore.Utils.Title;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Interfaces.ItemConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallbackActions {

    private final Map<String, Callback> callbackActions = new HashMap<>();
    private final MessageGesturePaper messageGesturePaper;

    public CallbackActions(MessageGesturePaper messageGesturePaper) {
        clearCallbacks();
        this.messageGesturePaper = messageGesturePaper;
    }

    public void registerCallback(String name, Callback callback) {
        callbackActions.put(name, callback);
    }

    public void clearCallbacks() {
        callbackActions.clear();
    }

    public Callback getCallback(String name) {
        return callbackActions.get(name);
    }

    public boolean hasCallback(String name) {
        return callbackActions.containsKey(name);
    }

    public Map<String, Callback> getCallbackActions() {
        return callbackActions;
    }

    public void removeCallback(String name) {
        callbackActions.remove(name);
    }

    // Custom ExecuteCommand
    public void executeCommand(String type, String command, InventoryClickEvent inventoryClickEvent, List<?> items) {
        Player p = (Player) inventoryClickEvent.getWhoClicked();
        TextComponent mainComponent = null;
        Title title;
        switch (type.toUpperCase()) {
            case "CLOSE":
                p.closeInventory();
                break;
            case "OPEN":
                Main.instance.getConfigGestion().getInterfaces().get(command).openInterface(items, p, 1);
                break;
            case "CALLBACK":
                getCallback(command).execute(inventoryClickEvent, items);
                break;
            case "CONSOLE":
                Bukkit.dispatchCommand(Main.instance.getServer().getConsoleSender(), messageGesturePaper.translate(p, command));
                break;
            case "OP":
                boolean wasOP = p.isOp();
                p.setOp(true);
                Bukkit.dispatchCommand(p, messageGesturePaper.translate(p, command).replace("{0}", p.getName()));
                p.setOp(wasOP);
                break;
            case "ADVENTURE-MESSAGE":
                p.sendMessage(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, command)));
                break;
            case "SOUND":
                String[] _sound = command.split("%splitter%");
                SoundType soundType = new SoundType(_sound[0],
                        _sound.length > 1 ? Double.parseDouble(_sound[1]) : 100,
                        _sound.length > 2 ? Double.parseDouble(_sound[2]) : 1.00
                );
                soundType.playSound(p);
                break;
            case "MESSAGE":
                if (command.contains("%enter%")) {
                    String[] y = command.split("%enter%");
                    boolean check = true;
                    TextComponent secondComponent = null;
                    for (String row : y) {
                        if (check) {
                            mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, row)));
                            check = false;
                            continue;
                        }
                        secondComponent = new TextComponent(messageGesturePaper.applyColorLegacy("\n" + messageGesturePaper.translate(p, row)));
                        mainComponent.addExtra(secondComponent);
                    }
                } else {
                    mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, command)));
                }
                p.spigot().sendMessage(mainComponent);
                break;
            case "MESSAGE&LINK":
                String[] zLINK = command.split("%splitter%");

                if (zLINK[0].contains("%enter%")) {
                    String[] y = zLINK[0].split("%enter%");
                    boolean check = true;
                    TextComponent secondComponent = null;
                    for (String row : y) {
                        if (check) {
                            mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, row)));
                            mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, zLINK[1]));
                            if (zLINK.length > 2) {
                                mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zLINK[2]))).create()));
                            }
                            check = false;
                            continue;
                        }
                        secondComponent = new TextComponent(messageGesturePaper.applyColorLegacy("\n" + messageGesturePaper.translate(p, row)));
                        secondComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, zLINK[1]));
                        if (zLINK.length > 2) {
                            secondComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zLINK[2]))).create()));
                        }
                        mainComponent.addExtra(secondComponent);
                    }
                } else {
                    mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zLINK[0])));
                    if (zLINK.length > 2) {
                        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zLINK[2]))).create()));
                    }
                    mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, zLINK[1]));
                }
                p.spigot().sendMessage(mainComponent);
                break;
            case "MESSAGE&SUGGEST_COMMAND":
                String[] zSUGGEST_COMMAND = command.split("%splitter%");

                if (zSUGGEST_COMMAND[0].contains("%enter%")) {
                    String[] y = zSUGGEST_COMMAND[0].split("%enter%");
                    boolean check = true;
                    TextComponent secondComponent = null;
                    for (String row : y) {
                        if (check) {
                            mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, row)));
                            mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, zSUGGEST_COMMAND[1]));
                            if (zSUGGEST_COMMAND.length > 2) {
                                mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zSUGGEST_COMMAND[2]))).create()));
                            }
                            check = false;
                            continue;
                        }
                        secondComponent = new TextComponent(messageGesturePaper.applyColorLegacy("\n" + messageGesturePaper.translate(p, row)));
                        secondComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, zSUGGEST_COMMAND[1]));
                        if (zSUGGEST_COMMAND.length > 2) {
                            secondComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zSUGGEST_COMMAND[2]))).create()));
                        }
                        mainComponent.addExtra(secondComponent);
                    }
                } else {
                    mainComponent = new TextComponent(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zSUGGEST_COMMAND[0])));
                    if (zSUGGEST_COMMAND.length > 2) {
                        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, zSUGGEST_COMMAND[2]))).create()));
                    }
                    mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, zSUGGEST_COMMAND[1]));
                }
                p.spigot().sendMessage(mainComponent);
                break;
            case "PLAYER":
                Bukkit.dispatchCommand(p, command);
                break;
            case "TITLE&SUBTITLE":
                String[] x = messageGesturePaper.applyColorLegacy(command).split("%splitter%");
                title = new Title(messageGesturePaper, messageGesturePaper.applyColorLegacy(x[0]), messageGesturePaper.applyColorLegacy(x[1]), 20, 50, 20);
                title.send(p);
                break;
            case "TITLE":
                title = new Title(messageGesturePaper, messageGesturePaper.applyColorLegacy(command), "", 20, 50, 20);
                title.send(p);
                break;
            case "SUBTITLE":
                title = new Title(messageGesturePaper, "", messageGesturePaper.applyColorLegacy(command), 20, 50, 20);
                title.send(p);
                break;
            case "ADVENTURE-TITLE&SUBTITLE":
                String[] titleParts = command.split("%splitter%");

                title = new Title(messageGesturePaper, titleParts[0], titleParts[1],
                        titleParts.length > 2 ? Integer.parseInt(titleParts[2]) : 5000,
                        titleParts.length > 3 ? Integer.parseInt(titleParts[3]) : 15000,
                        titleParts.length > 4 ? Integer.parseInt(titleParts[4]) : 5000
                );

                title.sendRich(p);
                break;
            case "ADVENTURE-TITLE":
                String[] titleParts_onlyTitle = command.split("%splitter%");
                title = new Title(messageGesturePaper,
                        titleParts_onlyTitle[0],
                        "",
                        titleParts_onlyTitle.length > 1 ? Integer.parseInt(titleParts_onlyTitle[1]) : 5000,
                        titleParts_onlyTitle.length > 2 ? Integer.parseInt(titleParts_onlyTitle[2]) : 15000,
                        titleParts_onlyTitle.length > 3 ? Integer.parseInt(titleParts_onlyTitle[3]) : 5000
                );

                title.sendRich(p);
                break;
            case "ADVENTURE-SUBTITLE":
                title = new Title(messageGesturePaper, "", command, 5000, 15000, 5000);
                title.sendRich(p);
                break;
            case "ACTIONBAR":
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(messageGesturePaper.applyColorLegacy(messageGesturePaper.translate(p, command))));
                break;
            default:
                break;
        }
    }

    public void executeActions(Map<String, ItemConfig> items, List<String> slots, ClickType clickType,
                               InventoryClickEvent inventoryClickEvent, List<?> itemsDb) {
        Map<String, List<String>> actions = items.get(slots.get(inventoryClickEvent.getSlot())).getActions();
        List<String> actionsList = actions.get(clickType.name());
        if (actionsList == null) {
            return;
        }
        // Clicked Type execution
        executeAction(actionsList, inventoryClickEvent, itemsDb);
        // All execution
        executeAction(actions.get("ALL"), inventoryClickEvent, itemsDb);
    }

    private void executeAction(List<String> actionsList, InventoryClickEvent inventoryClickEvent, List<?> items) {
        for (String execute : actionsList) {
            if (execute.contains(":")) {
                String[] split = execute.split(":");
                executeCommand(split[0], split[1], inventoryClickEvent, items);
            } else {
                executeCommand(execute, null, inventoryClickEvent, items);
            }
        }
    }
}
