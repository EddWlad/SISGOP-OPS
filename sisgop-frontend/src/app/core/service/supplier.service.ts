import { Injectable, inject } from '@angular/core';
import { GenericService } from './generic.service';
import { environment } from 'environments/environment.development';
import { Supplier } from '@core/models/supplier.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SupplierService extends GenericService<Supplier> {
  constructor() {
    super(
      inject(HttpClient),
      `${environment.HOST}/suppliers`
    );
  }
}
