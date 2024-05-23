package net.maksy.grimoires.modules.mysteries;

import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

public class DecryptionProcess {

    public static Map<UUID, DecryptionProcess> Decryptions = new TreeMap<>();

    public final Player player;
    public final Grimoire grimoire;
    public final List<Pair<String, Boolean>> decryption = new ArrayList<>();
    public DecryptionProcess(Player player, Grimoire grimoire) {
        this.player = player;
        this.grimoire = grimoire;
        for(String key : grimoire.getEncryptionKeys()) {
            decryption.add(Pair.of(key, false));
        }
        Decryptions.put(player.getUniqueId(), this);
    }

    public Player getPlayer() {
        return player;
    }

    public Grimoire getGrimoire() {
        return grimoire;
    }

    public void decrypt(String key) {
        for(int i = 0; i < decryption.size(); i++) {
            Pair<String, Boolean> pair = decryption.get(i);
            if(pair.getLeft().equals(key)) {
                if(grimoire.isExactOrder()) {
                    if(!isRecursiveDecrypted(i)) return;
                }
                decryption.set(i, Pair.of(key, true));
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
}
