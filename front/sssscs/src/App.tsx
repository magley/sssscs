import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import { Login } from './login/Login';
import { Link, Route, Routes } from 'react-router-dom';
import Register from './register/Register';
import './App.css';
import { MustBeLoggedIn, MustNotBeLoggedIn } from './auth/AuthGuard';
import { Home } from './home/Home';
import { AuthService } from './auth/AuthService';
import { CertSummary } from './certs/CertSummary';
import { Navbar } from './navbar/Navbar';
import { CertVerify } from './certs/CertVerify';
import { MyCertRequests } from './certs/requests/MyCertRequests';
import { CertRequestsIssuedToMe } from './certs/requests/CertRequestsIssuedToMe';
import { CertRequestCreate } from './certs/requests/CertRequestCreate';

// TODO: Explore the Context API. 
// We can share GlobalState without explicitly passing props.

export interface GlobalState {
    updateIsLoggedIn: () => void;
    isLoggedIn: boolean;
};

function App() {
    let [state, setState] = React.useState<GlobalState>({
        isLoggedIn: AuthService.isLoggedIn(),
        updateIsLoggedIn: () => {
            let newState = {...state};
            newState.isLoggedIn = AuthService.isLoggedIn();
    
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
                </Route>
                <Route element={<MustBeLoggedIn/>}>
                    <Route path="/certificates" element={<CertSummary />} />
                    <Route path="/certificates/verify" element={<CertVerify />} />
                    <Route path="/certificates/request/my" element={<MyCertRequests />} />
                    <Route path="/certificates/request/to-me" element={<CertRequestsIssuedToMe />} />
                    <Route path="/certificates/request/create" element={<CertRequestCreate />} />
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