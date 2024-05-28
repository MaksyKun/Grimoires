package net.maksy.grimoires.modules.mysteries;

import lombok.Getter;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.book_management.storage.GrimoireRegistry;
import net.maksy.grimoires.utils.ChatUT;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class DecryptionProcess implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static Map<UUID, DecryptionProcess> Decryptions = new TreeMap<>();

    @Getter
    public final UUID uuid;
    @Getter
    public final Grimoire grimoire;
    public final List<Pair<String, Boolean>> decryption = new ArrayList<>();

    public DecryptionProcess(Player player, Grimoire grimoire) {
        this.uuid = player.getUniqueId();
        this.grimoire = grimoire;
        for(String key : grimoire.getEncryptionKeys()) {
            decryption.add(Pair.of(key, false));
        }
    }

    public static DecryptionProcess get(Player player, Grimoire grimoire) {
        if(!Decryptions.containsKey(player.getUniqueId())) {
            Decryptions.put(player.getUniqueId(), Grimoires.sql().mysteries().getProcess(player, grimoire));
        }
        return Decryptions.get(player.getUniqueId());
    }

    public boolean isDecrypted(String word, int pos) {
        return decryption.get(pos).getLeft().equals(word) && decryption.get(pos).getRight();
    }

    public void decrypt(String key) {
        for(int i = 0; i < decryption.size(); i++) {
            Pair<String, Boolean> pair = decryption.get(i);
            Grimoires.consoleMessage(ChatUT.hexComp("&c" + pair.getLeft() + " " + pair.getRight()));
            if(pair.getLeft().equals(key)) {
                if(pair.getRight()) continue;
                if(grimoire.isExactOrder()) {
                    if(!isRecursiveDecrypted(i)) return;
                }
                Grimoires.consoleMessage(ChatUT.hexComp("&aSetting " + key + " to true"));
                decryption.set(i, Pair.of(key, true));
                Grimoires.sql().mysteries().addProcess(uuid, this);
                break;
            }
        }
    }

    private boolean isRecursiveDecrypted(int pos) {
        boolean isSolved = true;
        for(int i = 0; i < pos; i++) {
            Pair<String, Boolean> pair = decryption.get(i);
            if(!pair.getRight()) {
                isSolved = false;
                break;
            }
        }
        return isSolved;
    }

    public static List<String> getKeysOfGrimoire(Grimoire grimoire) {
        List<String> keys = new ArrayList<>();
        String indicator = MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator();
        for(String page : grimoire.getPages()) {
            if(page.contains(MysteryModule.getMysteriesCfg().getEncryptionAlgorithm().indicator())) {
                String[] words = page.split(" ");
                for(@RegExp String word : words) {
                    if(word.startsWith(indicator)) {
                        keys.add(word.replace(indicator, ""));
                    }
                }
            }
        }
        return keys;
    }
}
