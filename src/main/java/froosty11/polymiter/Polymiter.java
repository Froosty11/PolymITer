package froosty11.polymiter;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import froosty11.polymiter.item.ModItems;
import froosty11.polymiter.statuseffects.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Polymiter implements ModInitializer {
    public static final String MOD_ID = "polymiter";
    public static final Logger LOGGER = LoggerFactory.getLogger("PolymITer");


    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModStatusEffects.initialize();

        Identifier expectedId = Identifier.of(MOD_ID, "pirkko_banner_pattern");


        //polymer shish
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        PolymerResourcePackUtils.markAsRequired();

        LOGGER.info("PolymITer initialized.");
    }

    private void checkResource(String path) {
        InputStream stream = Polymiter.class.getResourceAsStream(path);
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ignored) {}
            LOGGER.info("Resource found on classpath: " + path);
        } else {
            LOGGER.warn("Resource NOT found on classpath: " + path);
        }
    }
}
