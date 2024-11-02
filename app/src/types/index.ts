export interface DsuPackage {
  id: string;
  name: string;
  version: string;
  size: number;
  architecture: string;
  minApiLevel: number;
  checksum: string;
}

export interface DeviceInfo {
  architecture: string;
  apiLevel: number;
  model: string;
  manufacturer: string;
}

export interface Settings {
  autoReboot: boolean;
  autoBackup: boolean;
  batteryCheck: boolean;
  darkMode: boolean;
  accentColor: string;
} 