import { DsuPackage, DeviceInfo } from '../types';
import { NativeModules } from 'react-native';

const { DsuModule } = NativeModules;

export class DsuService {
  static async checkCompatibility(dsu: DsuPackage): Promise<boolean> {
    const deviceInfo = await this.getDeviceInfo();
    return (
      dsu.architecture === deviceInfo.architecture &&
      dsu.minApiLevel <= deviceInfo.apiLevel
    );
  }

  static async applyDsu(dsu: DsuPackage): Promise<void> {
    return await DsuModule.applyDsu(dsu);
  }

  static async createBackup(): Promise<string> {
    const backupId = await DsuModule.createSystemBackup();
    return backupId;
  }

  static async restoreBackup(backupId: string): Promise<void> {
    await DsuModule.restoreSystemBackup(backupId);
  }

  static async getBatteryLevel(): Promise<number> {
    return await DsuModule.getBatteryLevel();
  }

  static async rebootDevice(): Promise<void> {
    await DsuModule.rebootDevice();
  }

  private static async getDeviceInfo(): Promise<DeviceInfo> {
    const info = await DsuModule.getDeviceInfo();
    return {
      architecture: info.architecture,
      apiLevel: info.apiLevel,
      model: info.model,
      manufacturer: info.manufacturer
    };
  }
} 