import { Link, NavLink, useNavigate } from "react-router-dom"
import { AuthService } from "../auth/AuthService"
import { GlobalState } from "../App"

export const Navbar = (props: {gloState: GlobalState}) => {

    const renderRoleNavbarItems = (role: string) => {
        switch (role) {
            case "REGULAR":
                return <NavbarItemsRegular gloState={props.gloState}/>
            case "ADMIN":
                return <NavbarItemsAdmin gloState={props.gloState}/>
            default:
                return <NavbarItemsLoggedOut gloState={props.gloState}/>
        }
    }

    return (
        <nav>
            <NavLink to="/">Home</NavLink>
            { renderRoleNavbarItems(props.gloState.role) }
        </nav>
    )
}

const NavbarItemsRegular = (props: {gloState: GlobalState}) => {
    const navigate = useNavigate();

    return (
        <>
            <NavLink to="/certificates">All Certificates</NavLink>
            <NavLink to="/certificates/request/create">Create New Certificate</NavLink>
            <NavLink to="/certificates/verify">Verify Certificates</NavLink>
            <NavLink to="/certificates/request/my">My Requests</NavLink>
            <NavLink to="/certificates/request/to-me">Requests Issued To Me</NavLink>
            
            <Link to="/" onClick={() => {
                    AuthService.removeJWT();      
                    navigate("/login");
                    props.gloState.updateIsLoggedIn();
                }}>Logout</Link>
        </>
    )    
}

const NavbarItemsAdmin = (props: {gloState: GlobalState}) => {
    const navigate = useNavigate();

    return (
        <>
            <NavLink to="/certificates">All Certificates</NavLink>
            <NavLink to="/certificates/request/create">Create New Certificate</NavLink>
            <NavLink to="/certificates/verify">Verify Certificates</NavLink>
            <NavLink to="/certificates/request/my">My Requests</NavLink>
            <NavLink to="/certificates/request/all">All Requests</NavLink>
            <NavLink to="/certificates/request/to-me">Requests Issued To Me</NavLink>
            
            <Link to="/" onClick={() => {
                    AuthService.removeJWT();      
                    navigate("/login");
                    props.gloState.updateIsLoggedIn();
                }}>Logout</Link>
        </>
    )    
}

const NavbarItemsLoggedOut = (props: {gloState: GlobalState}) => {
    return (
        <>
            <NavLink to="/login">Login</NavLink>
            <NavLink to="/register">Register</NavLink>
        </>
    )
}