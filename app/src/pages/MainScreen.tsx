import React, { useState } from 'react';
import { Box, Button, Typography, CircularProgress } from '@mui/material';
import { useDsu } from '../hooks/useDsu';
import { useSettings } from '../hooks/useSettings';
import { DsuList } from '../components/DsuList';
import { StatusIndicator } from '../components/StatusIndicator';
import { ErrorDialog } from '../components/ErrorDialog';
import { DsuService } from '../services/dsuService';

export const MainScreen = () => {
  const { currentDsu, isLoading, progress } = useDsu();
  const { settings } = useSettings();
  const [error, setError] = useState<string | null>(null);

  const handleApplyDsu = async () => {
    if (!currentDsu) return;
    
    try {
      // Check battery level
      if (settings.batteryCheck) {
        const batteryLevel = await DsuService.getBatteryLevel();
        if (batteryLevel < 20) {
          throw new Error('Battery level too low');
        }
      }

      // Backup current state
      if (settings.autoBackup) {
        await DsuService.createBackup();
      }

      // Apply DSU
      await DsuService.applyDsu(currentDsu);

      // Auto reboot if enabled
      if (settings.autoReboot) {
        await DsuService.rebootDevice();
      }
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Unknown error occurred');
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        DSU Sideloader
      </Typography>

      <DsuList />
      
      <StatusIndicator />

      {isLoading && (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <CircularProgress variant="determinate" value={progress} />
          <Typography>Loading DSU...</Typography>
        </Box>
      )}

      <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
        <Button
          variant="contained"
          size="large"
          onClick={handleApplyDsu}
          disabled={!currentDsu || isLoading}
        >
          Apply DSU
        </Button>
        <Button
          variant="outlined"
          size="large"
          onClick={() => DsuService.rebootDevice()}
        >
          Reboot Device
        </Button>
      </Box>

      <ErrorDialog error={error} onClose={() => setError(null)} />
    </Box>
  );
}; 