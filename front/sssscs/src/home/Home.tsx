import { AuthService } from "../auth/AuthService"

export const Home = () => {
    return (
        <b>
            {AuthService.getEmail()}
        </b>
    )
}