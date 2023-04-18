// Axios instance with an interceptor that puts the JWT from localstorage into Authorization: Bearer.

import axios, { InternalAxiosRequestConfig } from "axios";
import { Env } from "../common/Environment";
import { AuthService } from "../auth/AuthService";

export const axiosInstance = axios.create({
    baseURL: `${Env.url}/api/`
});

axiosInstance.interceptors.request.use(
    (value: InternalAxiosRequestConfig<any>) => {
        value.headers.Authorization = `Bearer ${AuthService.getJWTString()}`;
        return value;
    }
);