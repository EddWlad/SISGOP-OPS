import { inject, Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Material } from '@core/models/material.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class MaterialService extends GenericService<Material> {

  constructor() {
    super(
      inject(HttpClient),
      `${environment.HOST}/materials` // Use the environment variable for the API URL
    )
  }

  saveFile(data: File) {
    const formData: FormData = new FormData();
    formData.append('file', data);
    return this.http.post(`${environment.HOST}/materials/upload`, formData);
  }
}
