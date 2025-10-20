import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MeasurementUnitService } from 'app/core/service/measurement-unit.service';

export interface DialogData {
  idMeasurementUnit: string;
  unitMeasurementName: string;
}

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrl: './delete.component.scss',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButtonModule,
    MatDialogClose,
  ],
})
export class DeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<DeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public measurementService: MeasurementUnitService,
  ) { }
  confirmDelete(): void {
    this.measurementService.delete(this.data.idMeasurementUnit).subscribe({
      next: (response) => {
        this.dialogRef.close(response);
      },
      error: (error) => {
        console.error('Delete Error:', error);
      },
    });
  }
}
