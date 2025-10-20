import { inject, Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment.development';
import { MeasurementUnit } from '@core/models/measurementUnit.model';

@Injectable({
  providedIn: 'root'
})
export class MeasurementUnitService extends GenericService<MeasurementUnit> {
  constructor() {
    super(
      inject(HttpClient),
      `${environment.HOST}/measurement-units`
    );
  }

}
