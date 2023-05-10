import { Navigate, Outlet } from "react-router-dom";
import { AuthService } from "./AuthService";

export const MustNotBeLoggedIn = () => {
    return (
        !AuthService.isLoggedIn() ? <Outlet /> : <Navigate to={'/'} replace />
    );
}

export const MustBeAllowedRole = ({allowedRoles}: {allowedRoles: string[]}) => {
    return (
        allowedRoles.find(role => role === AuthService.getRole())
        ? <Outlet /> : <Navigate to={'/'} replace />
    )
}