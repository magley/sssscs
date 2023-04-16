import * as React from 'react';
import useMediaQuery from '@mui/material/useMediaQuery';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Login } from './login/Login';
import { NavLink, Route, Routes } from 'react-router-dom';
import Register from './register/Register';
import './App.css';
import { MustBeLoggedIn, MustNotBeLoggedIn } from './auth/AuthGuard';
import { Home } from './home/Home';
import { AuthService } from './auth/AuthService';
import { CertMain } from './certs/CertMain';
import { Navbar } from './navbar/Navbar';

export interface GlobalState {
    updateLoggedIn: () => void;
    isLoggedIn: boolean;
};

function App() {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const theme = React.useMemo(
        () =>
            createTheme({
                palette: {
                    mode: prefersDarkMode ? 'dark' : 'light',
                },
            }),
        [prefersDarkMode],
    );

    let [state, setState] = React.useState<GlobalState>({
        isLoggedIn: false,
        updateLoggedIn: () => {
            let newState = {...state};
            newState.isLoggedIn = AuthService.isLoggedIn();
    
            setState(newState);
        }
    });

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Navbar gloState={state}/>

            <Routes>
                <Route path="/" element={<Home/>} />

                <Route element={<MustNotBeLoggedIn/>}>
                    <Route path='login' element={<Login gloState={state}/>} />
                    <Route path='register' element={<Register />} />
                </Route>
                <Route element={<MustBeLoggedIn/>}>
                    <Route path="/certificates" element={<CertMain />} />
                </Route>

                <Route path="*" element={<Page404 />} />
            </Routes>
        </ThemeProvider>
    );
}

function Page404() {
    return (
        <b>404 not found.</b>
    )
}

export default App;