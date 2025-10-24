import { MeasurementUnit } from "./measurementUnit.model";

export class Material {
  idMaterial: string;
  materialName: string;
  materialDescription: string;
  measurementUnit: MeasurementUnit;
  materialImages: string[] = [
    "assets/images/user/no-image.png",
  ];
  status: number;
}
