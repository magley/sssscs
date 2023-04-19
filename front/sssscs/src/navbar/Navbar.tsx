import { Link, NavLink, useNavigate } from "react-router-dom"
import { AuthService } from "../auth/AuthService"
import { GlobalState } from "../App"

export const Navbar = (props: {gloState: GlobalState}) => {
    return (
        <nav>
            <NavLink to="/">Home</NavLink>
            {
                props.gloState.isLoggedIn ? 
                    <NavbarItemsLoggedIn gloState={props.gloState}/> : 
                    <NavbarItemsLoggedOut gloState={props.gloState}/>
            }
        </nav>
    )
}

const NavbarItemsLoggedIn = (props: {gloState: GlobalState}) => {
    const navigate = useNavigate();

    return (
        <>
            <NavLink to="/certificates">All Certificates</NavLink>
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

const NavbarItemsLoggedOut = (props: {gloState: GlobalState}) => {
    return (
        <>
            <NavLink to="/login">Login</NavLink>
            <NavLink to="/register">Register</NavLink>
        </>
    )
}