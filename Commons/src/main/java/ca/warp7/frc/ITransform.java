package ca.warp7.frc;

@FunctionalInterface
public
interface ITransform<T, R> {
    R apply(T t, T other);
}
