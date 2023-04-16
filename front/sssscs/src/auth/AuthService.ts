import {Buffer} from 'buffer';

export interface JWTStruct {
    sub: string,
    role: string,
    id: number,
    exp: number,
    iat: number,
};

export class AuthService {
    private static JWT_KEY = "JWT";

    static putToken(jwt: string): void {
        localStorage.setItem(this.JWT_KEY, jwt);
    }

    static delToken(): void {
        localStorage.removeItem(this.JWT_KEY);
    }

    private static getJWT(jwt: string): JWTStruct {
        let parts = jwt.split(".");
        for (let i = 0; i < parts.length; i++) {
            parts[i] = Buffer.from(parts[i], 'base64').toString(); 
        }
        let token: JWTStruct = JSON.parse(parts[1]);
        return token;
    }

    static isLoggedIn(): boolean {
        return this.getId() !== -1;
    }

    static getId(): number {
        const jwt = localStorage.getItem(this.JWT_KEY);
        if (jwt == null) {
            return -1;
        }
        return this.getJWT(jwt).id;
    }

    static getEmail(): string {
        const jwt = localStorage.getItem(this.JWT_KEY);
        if (jwt == null) {
            return "";
        }
        return this.getJWT(jwt).sub;
    }

    static getRole(): string {
        const jwt = localStorage.getItem(this.JWT_KEY);
        if (jwt == null) {
            return "";
        }
        return this.getJWT(jwt).role;
    }
};