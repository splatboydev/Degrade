package xyz.elevated.frequency;

import lombok.Getter;
import org.bukkit.Bukkit;
import xyz.elevated.frequency.data.type.PlayerDataManager;
import xyz.elevated.frequency.listener.PlayerListener;
import xyz.elevated.frequency.processor.ProcessorManager;
import xyz.elevated.frequency.tick.TickManager;

import me.godead.lilliputian.Dependency;
import me.godead.lilliputian.Lilliputian;
import me.godead.lilliputian.Repository;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public enum Frequency {
    INSTANCE;

    private FrequencyPlugin plugin;

    private final ProcessorManager processorManager = new ProcessorManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final TickManager tickManager = new TickManager();

    private final Executor executorAlert = Executors.newSingleThreadExecutor();
    private final Executor executorPacket = Executors.newSingleThreadExecutor();

    public void start(FrequencyPlugin plugin) {

        this.plugin = plugin;

        assert plugin != null : "Something went wrong! The plugin was null. (Startup)";

        Lilliputian lilliputian = new Lilliputian(plugin);
        lilliputian.getDependencyBuilder()
                .addDependency(new Dependency(
                        "https://hub.spigotmc.org/nexus/content/groups/public/",
                        "org.atteo.classindex",
                        "classindex",
                        "3.11"))
                .addDependency(new Dependency(
                        "https://hub.spigotmc.org/nexus/content/groups/public/",
                        "org.mongodb",
                        "mongo-java-driver",
                        "3.5.0"))
                .loadDependencies();

        tickManager.start();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    public void stop(FrequencyPlugin plugin) {
        this.plugin = plugin;

        assert plugin != null : "Something went wrong! The plugin was null. (Shutdown)";

        tickManager.stop();
    }
}
