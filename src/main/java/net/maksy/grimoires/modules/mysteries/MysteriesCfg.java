package net.maksy.grimoires.modules.mysteries;

import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.YamlParser;

public class MysteriesCfg {

    private final YamlParser config;

    public MysteriesCfg() {
        config = YamlParser.loadOrExtract(Grimoires.getInstance(), "Features/Mysteries.yml");
    }

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return new EncryptionAlgorithm(
                config.getBoolean("Enabled"),
                config.getString("Encryption.Indicator"),
                config.getInt("Encryption.WordLimit"),
                config.getString("Encryption.Design"),
                config.getString("Encryption.SolvedColor"),
                config.getBoolean("Encryption.ResetOnFail"));
    }
}
