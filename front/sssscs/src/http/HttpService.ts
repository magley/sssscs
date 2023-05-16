// Axios instance with an interceptor that puts the JWT from localstorage into Authorization: Bearer.

import axios, { AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { Env } from "../common/Environment";
import { AuthService } from "../auth/AuthService";

export const axiosInstance = axios.create({
    baseURL: `${Env.url}/api/`,
    headers: {
        "Content-Type": "application/json"
    }
});

axiosInstance.interceptors.request.use(
    (value: InternalAxiosRequestConfig<any>) => {
        value.headers.Authorization = `Bearer ${AuthService.getJWTString()}`;
        return value;
    }
);

axiosInstance.interceptors.response.use(
    (response: AxiosResponse<any, any>) => {
        return response;
    },
    (error: any) => {
        if (error.response.status === 401) {
            AuthService.removeJWT();      
            window.location.href = "/login";
            // Changing GloState?
        } 
        return Promise.reject(error);
    }
)