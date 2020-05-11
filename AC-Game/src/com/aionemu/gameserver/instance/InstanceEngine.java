package com.aionemu.gameserver.instance;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.WorldMapInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author ATracer
 */
public class InstanceEngine implements GameEngine {

    private static final Logger log = LoggerFactory.getLogger(InstanceEngine.class);
    private static ScriptManager scriptManager = new ScriptManager();
    public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/instancehandlers.xml");
    public static final InstanceHandler DUMMY_INSTANCE_HANDLER = new GeneralInstanceHandler();
    private Map<Integer, Class<? extends InstanceHandler>> handlers = new HashMap<Integer, Class<? extends InstanceHandler>>();

    @Override
    public void load(CountDownLatch progressLatch) {
        log.info("Instance engine load started");
        scriptManager = new ScriptManager();

        AggregatedClassListener acl = new AggregatedClassListener();
        acl.addClassListener(new OnClassLoadUnloadListener());
        acl.addClassListener(new ScheduledTaskClassListener());
        acl.addClassListener(new InstanceHandlerClassListener());
        scriptManager.setGlobalClassListener(acl);

        try {
            scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
            log.info("Loaded " + handlers.size() + " instance handlers.");
        } catch (Exception e) {
            throw new GameServerError("Can't initialize instance handlers.", e);
        } finally {
            if (progressLatch != null) {
                progressLatch.countDown();
            }
        }
    }

    @Override
    public void shutdown() {
        log.info("Instance engine shutdown started");
        scriptManager.shutdown();
        scriptManager = null;
        handlers.clear();
        log.info("Instance engine shutdown complete");
    }

    public void reload() {
        Util.printSection("Instances");
        log.info("Reloading instances");
        ScriptManager tmpSM;
        try {
            tmpSM = new ScriptManager();
            AggregatedClassListener acl = new AggregatedClassListener();
            acl.addClassListener(new OnClassLoadUnloadListener());
            acl.addClassListener(new ScheduledTaskClassListener());
            acl.addClassListener(new InstanceHandlerClassListener());
            tmpSM.setGlobalClassListener(acl);
            try {
                tmpSM.load(INSTANCE_DESCRIPTOR_FILE);
            } catch (Exception e) {
                throw new GameServerError("Error", e);
            }
        } catch (Exception e) {
            throw new GameServerError("Error", e);
        }
        if (tmpSM != null) {
            shutdown();
            load(null);
        }
    }

    public InstanceHandler getNewInstanceHandler(int worldId) {
        Class<? extends InstanceHandler> instanceClass = handlers.get(worldId);
        InstanceHandler instanceHandler = null;
        if (instanceClass != null) {
            try {
                instanceHandler = instanceClass.newInstance();
            } catch (Exception ex) {
                log.warn("Can't instantiate instance handler " + worldId, ex);
            }
        }
        if (instanceHandler == null) {
            instanceHandler = DUMMY_INSTANCE_HANDLER;
        }
        return instanceHandler;
    }

    /**
     * @param handler
     */
    final void addInstanceHandlerClass(Class<? extends InstanceHandler> handler) {
        InstanceID idAnnotation = handler.getAnnotation(InstanceID.class);
        if (idAnnotation != null) {
            handlers.put(idAnnotation.value(), handler);
        }
    }

    /**
     * @param instance
     */
    public void onInstanceCreate(WorldMapInstance instance) {
        instance.getInstanceHandler().onInstanceCreate(instance);
    }

    public static final InstanceEngine getInstance() {
        return SingletonHolder.instance;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final InstanceEngine instance = new InstanceEngine();
    }
}