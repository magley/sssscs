import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import { Login } from './login/Login';
import { Link, Route, Routes } from 'react-router-dom';
import { Register } from './register/Register';
import './App.css';
import { MustBeAllowedRole, MustNotBeLoggedIn } from './auth/AuthGuard';
import { Home } from './home/Home';
import { AuthService } from './auth/AuthService';
import { CertSummary } from './certs/CertSummary';
import { Navbar } from './navbar/Navbar';
import { CertVerify } from './certs/CertVerify';
import { CertRequests } from './certs/requests/CertRequests';
import { CertRequestsIssuedToMe } from './certs/requests/CertRequestsIssuedToMe';
import { CertRequestCreate } from './certs/requests/CertRequestCreate';
import { VerifyPage } from './verify/VerifyPage';
import { ResetPassword } from './password/ResetPassword';
import { CertRequestService } from './certs/requests/CertRequestService';
import { RotatePassword } from './password/RotatePassword';

// TODO: Explore the Context API. 
// We can share GlobalState without explicitly passing props.

export interface GlobalState {
    updateIsLoggedIn: () => void;
    isLoggedIn: boolean;
    role: string;
};

function App() {
    let [state, setState] = React.useState<GlobalState>({
        isLoggedIn: AuthService.isLoggedIn(),
        role: AuthService.getRole(),
        updateIsLoggedIn: () => {
            let newState = {...state};
            newState.isLoggedIn = AuthService.isLoggedIn();
            newState.role = AuthService.getRole();
    
            setState(newState);
        }
    });

    return (
        <>
            <CssBaseline />
            <Navbar gloState={state}/>

            <Routes>
                <Route path="/" element={<Home/>} />

                <Route element={<MustNotBeLoggedIn/>}>
                    <Route path='login' element={<Login gloState={state}/>} />
                    <Route path='register' element={<Register />} />
                    <Route path='verify' element={<VerifyPage />} />
                    <Route path='reset-password' element={<ResetPassword />} />
                    <Route path="/update-password" element={<RotatePassword />} />
                </Route>
                <Route element={<MustBeAllowedRole allowedRoles={["REGULAR", "ADMIN"]} />}>
                    <Route path="/certificates" element={<CertSummary />} />
                    <Route path="/certificates/verify" element={<CertVerify />} />
                    <Route path="/certificates/request/my" element={<CertRequests requestsProvider={CertRequestService.getOwn} />} />
                    <Route path="/certificates/request/to-me" element={<CertRequestsIssuedToMe />} />
                    <Route path="/certificates/request/create" element={<CertRequestCreate />} />     
                </Route>
                <Route element={<MustBeAllowedRole allowedRoles={["ADMIN"]}/>}>
                    <Route path="/certificates/request/all" element={<CertRequests requestsProvider={CertRequestService.getAll}/>} />
                </Route>

                <Route path="*" element={<Page404 />} />
            </Routes>
        </>
    );
}

function Page404() {
    return (
        <>
            <b>404 not found.</b>
            <br/>
            <Link to="/">Return home</Link>
        </>
    )
}

export default App;