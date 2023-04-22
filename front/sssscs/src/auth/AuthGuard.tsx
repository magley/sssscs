import { Navigate, Outlet } from "react-router-dom";
import { AuthService } from "./AuthService";

export const MustBeLoggedIn = () => {
    return (
        AuthService.isLoggedIn() ? <Outlet /> : <Navigate to={'/'} replace />
    );
}

export const MustNotBeLoggedIn = () => {
    return (
        !AuthService.isLoggedIn() ? <Outlet /> : <Navigate to={'/'} replace />
    );
}