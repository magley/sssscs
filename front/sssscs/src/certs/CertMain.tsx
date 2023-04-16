import { AuthService } from "../auth/AuthService"

export const CertMain = () => {
    return (
        <>
        <b>You can only see this if you're logged in.</b>
        <br/>
        <p>{AuthService.getEmail()}</p>
        </>
    )
}