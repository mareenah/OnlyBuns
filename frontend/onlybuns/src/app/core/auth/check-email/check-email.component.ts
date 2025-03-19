import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-check-email',
  templateUrl: './check-email.component.html',
  styleUrls: ['./check-email.component.css'],
})
export class CheckEmailComponent implements OnInit {
  verificationCode: String = '';
  verified: Boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.verificationCode = params['verificationCode'];
      this.authService.verify(this.verificationCode).subscribe({
        next: () => {
          this.verified = true;
          this.router.navigate(['/login']);
          alert('Congratulations, your account is verified successfully!');
        },
        error: () => {
          this.verified = false;
          this.router.navigate(['/register']);
          alert(
            'Sorry, we could not verify account. It maybe already verified or verification code is incorrect.'
          );
        },
      });
    });
    this.authService.verify;
  }
}
