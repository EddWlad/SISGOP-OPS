import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';

import { Component, Inject } from '@angular/core';
import { SupplierService } from '@core/service/supplier.service';
import { MatButtonModule } from '@angular/material/button';

export interface DialogData {
  idSupplier: string;
  supplierName: string;
  supplierRuc: string;
  supplierEmail: string;
  supplierPhone: string;
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
    public supplierService: SupplierService
  ) { }
  confirmDelete(): void {
    this.supplierService.delete(this.data.idSupplier).subscribe({
      next: () => this.dialogRef.close(true),   // <-- éxito
      error: (error) => {
        console.error('Delete Error:', error);
        this.dialogRef.close(false);            // <-- opcional: comunica fallo
      },
    });
  }
}
