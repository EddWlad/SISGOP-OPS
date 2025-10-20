import { inject, Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Company } from '@core/models/company.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CompanyService extends GenericService<Company> {
  constructor() {
    super(
      inject(HttpClient),
      `${environment.HOST}/companies`
    );
  }

  saveFile(data: File) {
    const formData: FormData = new FormData();
    formData.append('file', data);
    return this.http.post(`${environment.HOST}/companies/upload`, formData);
  }

}
