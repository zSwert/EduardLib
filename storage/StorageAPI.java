package net.eduard.api.lib.storage;

import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.swing.internal.plaf.synth.resources.synth;

import java.util.Random;
import java.util.UUID;

import net.eduard.api.lib.modules.Extra;
import net.eduard.api.lib.storage.java_storables.TimeStampStorable;
import net.eduard.api.lib.storage.java_storables.UUIDStorable;
import net.eduard.api.lib.storage.references.ReferenceBase;

/**
 * Sistema automatico de Armazenamento em Mapas que podem ser usados em JSON,
 * SQL, YAML<br>
 * 
 * 
 * 
 * 
 * @author Eduard
 * @version 1.3
 * @since Lib v1.0
 *
 */
public class StorageAPI {
	
	
	private static boolean debug = true;
	public static String STORE_KEY = "=";
	public static String REFER_KEY = "@";
	private static Map<Class<?>, Storable> storages = new LinkedHashMap<>();
	private static Map<Class<?>, String> aliases = new LinkedHashMap<>();
	private static Map<Integer, Object> objects = new LinkedHashMap<>();
	private static List<ReferenceBase> references = new ArrayList<>();

	public static boolean isStorable(Object object) {
		
		return object instanceof Storable;
	}

	public static boolean isStorable(Class<?> claz) {
		return Storable.class.isAssignableFrom(claz);
	}

	public static Object getObjectById(int id) {
		return objects.get(id);
	}

	private static int randomId() {
		return new Random().nextInt(100000);
	}

	public synchronized static int newId() {
		int id = 0;
		do {
			id = randomId();
		} while (objects.containsKey(id));
		return id;
	}

	/**
	 * Atualiza as Referencias criadas e usadas na config
	 */
	public static void updateReferences() {
		Iterator<ReferenceBase> loop = references.iterator();
		while (loop.hasNext()) {
			ReferenceBase next = loop.next();
			next.update();
			loop.remove();
		}
	}

	public synchronized static void newReference(ReferenceBase refer) {
		references.add(refer);
		debug("<<----->> REFERENCE LINKED");
	}

	public synchronized static int getIdByObject(Object object) {
		for (Entry<Integer, Object> entry : objects.entrySet()) {
			if (entry.getValue().equals(object)) {
				return entry.getKey();
			}
		}
		int id = newId();
		objects.put(id, object);
		debug("<<>>" + object.getClass().getSimpleName() + "@" + id);
		return id;
	}

	public static Object store(StorageInfo store, Object object) {

		return new StorageObject(store).store(object);
	}

	public static Object restore(StorageInfo store, Object object) {

		return new StorageObject(store).restore(object);
	}

	public synchronized static void register(Class<? extends Storable> claz) {
		autoRegisterClass(claz);
	}
	public synchronized static void register(Class<? extends Storable> claz,String alias) {
		autoRegisterClass(claz,alias);
	} 
	public synchronized static void register(Class<?> claz, Storable storable) {
		storages.put(claz, storable);
		aliases.put(claz, claz.getSimpleName());
		debug("++ CLASS " + claz.getSimpleName());
	}

	public synchronized static void registerPackage(Class<?> clazzForPackage) {
		registerPackage(clazzForPackage, clazzForPackage.getPackage().getName());
	}

	public synchronized static void registerPackage(Class<?> clazzPlugin, String pack) {
		debug("<> PACKAGE " + pack);

		List<Class<?>> classes = Extra.getClasses(clazzPlugin, pack);
		for (Class<?> claz : classes) {
			autoRegisterClass(claz);
		}
	}
	public synchronized static Storable autoRegisterClass(Class<?> claz) {
		return autoRegisterClass(claz, claz.getSimpleName());
	}
	
	public synchronized static Storable autoRegisterClass(Class<?> claz,String alias) {
		Storable store = null;
		try {
			if (Modifier.isAbstract(claz.getModifiers())) {
				debug("== ABSTRACT CLASS " + claz.getSimpleName());
			} else if (claz.isEnum()) {
				debug("== ENUM CLASS " + claz.getSimpleName());
			} else if (claz.isAnonymousClass()) {
				debug("== ANONYMOUS CLASS " + claz.getSimpleName());
			} else if (claz.isInterface()) {
				debug("== INTERFACE " + claz.getSimpleName());
			} else if (isStorable(claz)) {
				store = (Storable) claz.newInstance();
				register(claz, store);
			} else {
				debug("-- CLASS " + claz.getSimpleName());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return store;
	}

	public synchronized static void registerClasses(Class<?> claz) {
		debug("<> CLASSES " + claz.getName());
		for (Class<?> anotherClass : claz.getDeclaredClasses()) {
			autoRegisterClass(anotherClass);
		}
	}

	public synchronized static void registerObject(int id, Object object) {
		objects.put(id, object);
		debug("+ OBJECT " + getAlias(object.getClass()) + "@" + id);
	}

	public synchronized static void unregisterObjectById(int id) {
		objects.remove(id);
		debug("- OBJECT @" + id);
	}

	public synchronized static void unregisterObject(Object object) {
		objects.remove(object);
		debug("- OBJECT " + getAlias(object.getClass()));
	}

	public synchronized static void unregisterStorable(Class<?> claz) {
		storages.remove(claz);
		debug("- CLASS " + claz.getName());

	}

	public static Object transform(Object object, Class<?> type) throws Exception {
		String fieldTypeName = Extra.toTitle(type.getSimpleName());
		Object value = Extra.getResult(Extra.class, "to" + fieldTypeName, Extra.getParameters(Object.class), object);
		if (value instanceof String) {
			value = Extra.toChatMessage((String) value);
		}
		return value;
	}

	public synchronized static Storable getStore(Class<?> claz) {
		if (claz == null)
			return null;
		Storable store = storages.get(claz);
		if (store == null) {
			for (Entry<Class<?>, Storable> entry : storages.entrySet()) {
				if (entry.getKey().isAssignableFrom(claz)) {
					store = entry.getValue();
				}
			}
		}
		return store;

	}

	public synchronized static String getAlias(Class<?> claz) {
		for (Entry<Class<?>, String> entry : aliases.entrySet()) {
			if (entry.getKey().equals(claz)) {
				return entry.getValue();
			}
		}
		for (Entry<Class<?>, String> entry : aliases.entrySet()) {
			if (entry.getKey().isAssignableFrom(claz)) {
				return entry.getValue();
			}
			if (claz.isAssignableFrom(entry.getKey())) {
				return entry.getValue();
			}
		}
		return claz.getSimpleName();

	}

	public synchronized static Class<?> getClassByAlias(String alias) {
		for (Entry<Class<?>, String> entry : aliases.entrySet()) {
			if (entry.getValue().equals(alias)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public synchronized static boolean isRegistred(Class<?> claz) {
		return storages.containsKey(claz);
	}

	public synchronized static Map<Class<?>, String> getAliases() {
		return aliases;
	}

	public static void setAliases(Map<Class<?>, String> aliases) {
		StorageAPI.aliases = aliases;
	}

//	public static Map<UUID, Object> getDatabase() {
//		return database;
//	}
//
//	public static void setDatabase(Map<UUID, Object> database) {
//		StorageAPI.database = database;
//	}

	static {
		StorageAPI.register(UUID.class, new UUIDStorable());
		StorageAPI.register(Timestamp.class, new TimeStampStorable());
	}

	public static void debug(String msg) {
		if (debug)
			System.out.println("[Storage] " + msg);
	}

	public static void registerAlias(Class<?> claz, String alias) {
		aliases.put(claz, alias);
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		StorageAPI.debug = debug;
	}

}
