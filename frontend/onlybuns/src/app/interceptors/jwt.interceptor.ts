import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ACCESS_TOKEN } from "src/app/models/constants";

@Injectable()
export class JwtInterceptor implements HttpInterceptor{
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const accessTokenRequest = req.clone({
            setHeaders: {
                Authorization: `Bearer ` + localStorage.getItem(ACCESS_TOKEN),
            }
        });
        return next.handle(accessTokenRequest);
    }
    
}