import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class Cloner {
    /**
     * Returns a MethodHandle receiving two instances of class_ and copies the values of all declared fields of the
     * second into the first.
     *
     * @param class_
     * @param lookup a MethodHandles.Lookup
     * @return
     */
    public static MethodHandle ofClass(Class<?> class_, MethodHandles.Lookup lookup) {
        Field[] fields = class_.getDeclaredFields();

        MethodHandle copy = MethodHandles.empty(MethodType.methodType(void.class));

        for (Field field : fields) {

            // we can't copy static fields between instances :)
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            MethodHandle get = null, set = null;

            try {
                get = lookup.unreflectGetter(field);
                set = lookup.unreflectSetter(field);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(
                    "given lookup unable to access field " + field.getName(),
                    e);
            }

            copy = MethodHandles.foldArguments(
                MethodHandles.filterArguments(set, 1, get),
                copy);
        }

        // catch and rethrow exceptions as RuntimeExceptions
        // we're unlikely to throw exceptions in the process of copying
        // primitives and references around so we might as well give a
        // minimally helpful message and move on.
        copy = MethodHandles.catchException(
            copy,
            Throwable.class,
            MethodHandles.explicitCastArguments(
                rethrowException,
                MethodType.methodType(void.class, Throwable.class, class_)));

        return copy;
    }

    private static MethodHandle rethrowException = null;

    static {
        try {
            rethrowException = MethodHandles.lookup().findStatic(
                Cloner.class,
                "rethrowException",
                MethodType.methodType(void.class, Throwable.class, Object.class));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "failed to lookup rethrowException", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                "unable to find internal rethrowException method", e);
        }
    }

    public static class CloningException extends RuntimeException {
        CloningException(String message, Throwable t) {
            super(message, t);
        }
    }

    private static void rethrowException(Throwable t, Object src) {
        throw new CloningException(
            String.format(
                "failed to copy instance of %s",
                src.getClass().getCanonicalName()),
            t);
    }
}
