package io.eliotesta98.VanillaChallenges.Interfaces;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class ItemConfig {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");
    private String name, type, texture, soundClick, nameItemConfig;
    private List<String> lore;

    public ItemConfig(String nameItemConfig, String name, String type, String texture, List<String> lore, String soundClick) {
        this.name = name;
        this.type = type;
        this.texture = texture;
        this.lore = lore;
        this.soundClick = soundClick;
        this.nameItemConfig = nameItemConfig;
    }

    @SuppressWarnings({"CallToPrintStackTrace", "deprecation"})
    public ItemStack createItemConfig(String currentInterface, int pageNumber, String nbt, int positionItem) {
        ItemStack item;
        if (nbt.split(";").length >= 17) {
            String type = nbt.split(";")[16].split(":")[1];
            if (!type.contains("-"))// controllo la versione per settare l'item
                item = new ItemStack(Material.getMaterial(type));
            else {
                String[] x = type.split("-");
                item = new ItemStack(Material.getMaterial(x[0]), 1, Short.parseShort(x[1]));
            }
        } else {
            if (!type.contains("-"))// controllo la versione per settare l'item
                item = new ItemStack(Material.getMaterial(type));
            else {
                String[] x = type.split("-");
                item = new ItemStack(Material.getMaterial(x[0]), 1, Short.parseShort(x[1]));
            }
        }
        if (type.equalsIgnoreCase("PLAYER_HEAD") || type.contains("SKULL")) {
            if (!texture.equalsIgnoreCase("")) {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                try {
                    PlayerProfile player_profile = Bukkit.createPlayerProfile(RANDOM_UUID);
                    PlayerTextures textures = player_profile.getTextures();
                    textures.setSkin(getUrlFromBase64(texture));
                    player_profile.setTextures(textures);
                    meta.setOwnerProfile(player_profile);
                } catch (NoSuchMethodError | MalformedURLException ignored) {
                    GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                    profile.getProperties().put("textures", new Property("textures", texture));
                    Field profileField;
                    try {
                        profileField = meta.getClass().getDeclaredField("profile");
                        profileField.setAccessible(true);
                        profileField.set(meta, profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                item.setItemMeta(meta);
            } /*else if (!generator.getNomePlayer().equalsIgnoreCase("")) {
                // prendo la testa
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setOwner(generator.getNomePlayer());
                item.setItemMeta(meta);
            }*/
        }
        ItemMeta itemm = item.getItemMeta();
        if (!lore.isEmpty()) {
            ArrayList<String> lorenew = new ArrayList<>();
            for (String lorePart : lore) {
                if (lorePart.contains("{time}")) {
                    lorenew.add(ColorUtils.applyColor(lorePart.replace("{time}", nbt.split(";")[2].split(":")[1])));
                } else if (lorePart.contains("{challengeDescription}")) {
                    String[] description = nbt.split(";")[3].split(":")[1].split(",");
                    lorenew.add(ColorUtils.applyColor(lorePart.replace("{challengeDescription}", "")));
                    for (String rigo : description) {
                        if (rigo.contains("[") || rigo.contains("]")) {
                            continue;
                        }
                        if (rigo.contains("{hours}")) {
                            lorenew.add(ColorUtils.applyColor(rigo.replace("{hours}", nbt.split(";")[2].split(":")[1])));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(rigo));
                    }
                } else if (lorePart.contains("{challengePoint}")) {
                    lorenew.add(ColorUtils.applyColor(lorePart.replace("{challengePoint}", nbt.split(";")[4].split(":")[1])));
                } else if (lorePart.contains("{challengeItemsInHand}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[5].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeItemsInHand}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeItemsInHand}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{words}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[6].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{words}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{words}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{word}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{word}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{word}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeBlocks}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[7].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeBlocks}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeBlocks}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{rewards}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description;
                    String[] numbers;
                    if (nbt.split(";")[8].contains(",")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{rewards}", "")));
                        if (nbt.split(";")[8].split(",")[0].split(":").length == 3) {
                            String[] descriptions = nbt.split(";")[8].split(",");
                            for (String s : descriptions) {
                                String[] rigo = s.split(":");
                                String aDescription;
                                String aNumber;
                                if (rigo.length == 3) {
                                    aDescription = rigo[1].substring(1);
                                    aNumber = rigo[2];
                                } else {
                                    aDescription = rigo[0].substring(1);
                                    aNumber = rigo[1];
                                }
                                if (aNumber.contains("]")) {
                                    aNumber = aNumber.substring(0, aNumber.length() - 1);
                                }
                                if (aDescription.contains("command")) {
                                    String[] commandPart = aNumber.split(" ");
                                    Material reward = Material.AIR;
                                    int numberRew = 0;
                                    for (String part : commandPart) {
                                        try {
                                            numberRew = Integer.parseInt(part);
                                        } catch (Exception ex) {
                                            if (reward == Material.AIR) {
                                                reward = Material.getMaterial(part);
                                            }
                                        }
                                    }
                                    if (reward == null) {
                                        continue;
                                    }
                                    lorenew.add(ColorUtils.applyColor(itemRigo.replace("{reward}", reward + "").replace("{number}", numberRew + "")));
                                    continue;
                                } else if (aDescription.equalsIgnoreCase("[]")) {
                                    continue;
                                }
                                lorenew.add(ColorUtils.applyColor(itemRigo.replace("{reward}", aDescription).replace("{number}", aNumber)));
                            }
                            continue;
                        } else {
                            description = nbt.split(";")[8].split(",")[0].split(":");
                            numbers = nbt.split(";")[8].split(",")[1].split(":");
                        }
                    } else {
                        description = nbt.split(";")[8].split(":")[1].split(",");
                        numbers = nbt.split(";")[8].split(":")[2].split(",");
                    }
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{rewards}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{rewards}", "")));
                    }
                    for (int i = 0; i < description.length; i++) {
                        String rigo = description[i].substring(1);
                        String number = numbers[i].substring(0, numbers[i].length() - 1);
                        if (rigo.contains("command")) {
                            String[] commandPart = number.split(" ");
                            Material reward = null;
                            int numberRew = 0;
                            for (String part : commandPart) {
                                try {
                                    numberRew = Integer.parseInt(part);
                                } catch (Exception ex) {
                                    if (reward == null) {
                                        reward = Material.getMaterial(part);
                                    }
                                }
                            }
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{reward}", reward + "").replace("{number}", numberRew + "")));
                            continue;
                        } else if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{reward}", rigo).replace("{number}", number)));
                    }
                } else if (lorePart.contains("{challengeSneaking}")) {
                    String result = nbt.split(";")[9].split(":")[1];
                    if (!result.equalsIgnoreCase("NOBODY") && !result.equalsIgnoreCase("null")) {
                        lorenew.add(ColorUtils.applyColor(lorePart.replace("{challengeSneaking}", nbt.split(";")[9].split(":")[1])));
                    }
                } else if (lorePart.contains("{challengeBlocksOnPlane}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[10].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeBlocksOnPlane}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeBlocksOnPlane}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{block}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeVehicles}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[11].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeVehicles}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeVehicles}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{vehicle}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{vehicle}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{vehicle}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeMobs}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[12].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeMobs}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeMobs}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{mob}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{mob}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{mob}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeItems}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[13].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeItems}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeItems}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{item}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeCauses}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[14].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeCauses}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeCauses}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{cause}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{cause}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{cause}", rigo.substring(1))));
                    }
                } else if (lorePart.contains("{challengeColors}")) {
                    String itemRigo = lorePart.split(Pattern.quote("."))[1];
                    String initialPart = lorePart.split(Pattern.quote("."))[0];
                    String[] description = nbt.split(";")[15].split(":")[1].split(",");
                    if (description.length != 1) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeColors}", "")));
                    } else if (!description[0].equalsIgnoreCase("[]")) {
                        lorenew.add(ColorUtils.applyColor(initialPart.replace("{challengeColors}", "")));
                    }
                    for (String rigo : description) {
                        if (rigo.equalsIgnoreCase("[]")) {
                            continue;
                        } else if (rigo.contains("]")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{color}", rigo.substring(1, rigo.length() - 1))));
                            continue;
                        } else if (rigo.contains("[")) {
                            lorenew.add(ColorUtils.applyColor(itemRigo.replace("{color}", rigo.substring(1))));
                            continue;
                        }
                        lorenew.add(ColorUtils.applyColor(itemRigo.replace("{color}", rigo.substring(1))));
                    }
                } else {
                    lorenew.add(ColorUtils.applyColor(lorePart));
                }
            }
            itemm.setLore(lorenew);
            item.setItemMeta(itemm);
        }
        String nameChange = name;
        if (name.contains("{number}")) {
            itemm.setDisplayName(ChatColor.translateAlternateColorCodes('&', nameChange.replace("{number}", "" + pageNumber)));
            item.setItemMeta(itemm);
        } else if (name.contains("{challengeName}")) {
            itemm = item.getItemMeta();
            if (nameChange.contains("{challengeName}")) {
                nameChange = name.replace("{challengeName}", nbt.split(";")[1].split(":")[1]);
            }
            itemm.setDisplayName(ChatColor.translateAlternateColorCodes('&', nameChange));
            item.setItemMeta(itemm);
        } else {
            itemm.setDisplayName(ChatColor.translateAlternateColorCodes('&', nameChange));
        }
        item.setItemMeta(itemm);
        NBTItem nbtItem = new NBTItem(item);
        if (!nbt.equalsIgnoreCase("")) {
            String[] nbtList = nbt.split(";");
            for (String nbtString : nbtList) {
                String[] nbtSplit = nbtString.split(":");
                try {
                    int numberPage = Integer.parseInt(nbtSplit[1]);
                    nbtItem.setInteger(nbtSplit[0], numberPage);
                } catch (Exception ex) {
                    nbtItem.setString(nbtSplit[0], nbtSplit[1]);
                }
            }
        }
        nbtItem.setInteger("vc.positionItem", positionItem);
        nbtItem.setString("vc.currentInterface", currentInterface);
        return nbtItem.getItem();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public String getSoundClick() {
        return soundClick;
    }

    public void setSoundClick(String soundClick) {
        this.soundClick = soundClick;
    }

    public String getNameItemConfig() {
        return nameItemConfig;
    }

    public void setNameItemConfig(String nameItemConfig) {
        this.nameItemConfig = nameItemConfig;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", texture='" + texture + '\'' +
                ", soundClick='" + soundClick + '\'' +
                ", nameItemConfig='" + nameItemConfig + '\'' +
                ", lore=" + lore +
                '}';
    }
}
