package jpf2019.wbs.update;

public class WBS_1c {

	public int change(int oldVal, int newVal){return oldVal;}
	public float change(float oldVal, float newVal) {return oldVal;}
	public double change(double oldVal, double newVal){return oldVal;}
	public boolean change(boolean oldVal, boolean newVal){return oldVal;}
	public long change(long oldVal, long newVal){return oldVal;}
	public final boolean OLD = true;
	public final boolean NEW = false;
	public boolean execute(boolean executionMode){return executionMode;};

	// Internal state
	private int WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;
	private int WBS_Node_WBS_BSCU_rlt_PRE1;
	private int WBS_Node_WBS_rlt_PRE2;

	// Outputs
	private int Nor_Pressure;
	private int Alt_Pressure;
	private int Sys_Mode;

	public WBS_1c() {
		WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = 0;
		WBS_Node_WBS_BSCU_rlt_PRE1 = 0;
		WBS_Node_WBS_rlt_PRE2 = 100;
		Nor_Pressure = 0;
		Alt_Pressure = 0;
		Sys_Mode = 0;
	}

	public void update(int PedalPos, boolean AutoBrake, boolean Skid) {
		int WBS_Node_WBS_AS_MeterValve_Switch;
		int WBS_Node_WBS_AccumulatorValve_Switch;
		int WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch;
		boolean WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator;
		int WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1;
		int WBS_Node_WBS_BSCU_Command_Switch;
		boolean WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6;
		int WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay;
		int WBS_Node_WBS_BSCU_Switch2;
		int WBS_Node_WBS_BSCU_Switch3;
		int WBS_Node_WBS_BSCU_Unit_Delay1;
		int WBS_Node_WBS_Green_Pump_IsolationValve_Switch;
		int WBS_Node_WBS_SelectorValve_Switch;
		int WBS_Node_WBS_SelectorValve_Switch1;
		int WBS_Node_WBS_Unit_Delay2;

		WBS_Node_WBS_Unit_Delay2 = WBS_Node_WBS_rlt_PRE2;
		WBS_Node_WBS_BSCU_Unit_Delay1 = WBS_Node_WBS_BSCU_rlt_PRE1;
		WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay = WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;

		WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator = change(WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay == 0, WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay<= 0);

		if ((PedalPos == 0)) {
			WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
		} else {
			if ((PedalPos == 1)) {
				WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 1;
			} else {
				if ((PedalPos == 2)) {
					WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 2;
				} else {
					if ((PedalPos == 3)) {
						WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 3;
					} else {
						if ((PedalPos == 4)) {
							WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 4;
						} else {
							WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
						}
					}
				}
			}
		}

		if (change(AutoBrake && WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator, AutoBrake == WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator)) {
			WBS_Node_WBS_BSCU_Command_Switch = 1;
		} else {
			WBS_Node_WBS_BSCU_Command_Switch = 0;
		}

		WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6 = ((((!(WBS_Node_WBS_BSCU_Unit_Delay1 == 0))
				&& (WBS_Node_WBS_Unit_Delay2 <= 0)) && WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator)
				|| (!WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator));

		if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
			if (Skid) {
				WBS_Node_WBS_BSCU_Switch3 = 0;
			} else {
				WBS_Node_WBS_BSCU_Switch3 = 4;
			}
		} else {
			WBS_Node_WBS_BSCU_Switch3 = 4;
		}

		if (change(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6, false)) {
			WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 0;
		} else {
			WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 5;
		}

		if ((WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1)) {
			WBS_Node_WBS_SelectorValve_Switch1 = 0;
		} else {
			WBS_Node_WBS_SelectorValve_Switch1 = 5;
		}

		if ((!WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6)) {
			WBS_Node_WBS_AccumulatorValve_Switch = 0;
		} else {
			if ((WBS_Node_WBS_SelectorValve_Switch1 >= 1)) {
				WBS_Node_WBS_AccumulatorValve_Switch = WBS_Node_WBS_SelectorValve_Switch1;
			} else {
				WBS_Node_WBS_AccumulatorValve_Switch = 5;
			}
		}

		if (change(WBS_Node_WBS_BSCU_Switch3 == 0, WBS_Node_WBS_BSCU_Switch3 <= 0)) {
			WBS_Node_WBS_AS_MeterValve_Switch = 0;
		} else {
			if ((WBS_Node_WBS_BSCU_Switch3 == 1)) {
				WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 4);
			} else {
				if ((WBS_Node_WBS_BSCU_Switch3 == 2)) {
					WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 2);
				} else {
					if ((WBS_Node_WBS_BSCU_Switch3 == 3)) {
						WBS_Node_WBS_AS_MeterValve_Switch = ((WBS_Node_WBS_AccumulatorValve_Switch / 4) * 3);
					} else {
						if ((WBS_Node_WBS_BSCU_Switch3 == 4)) {
							WBS_Node_WBS_AS_MeterValve_Switch = WBS_Node_WBS_AccumulatorValve_Switch;
						} else {
							WBS_Node_WBS_AS_MeterValve_Switch = 0;
						}
					}
				}
			}
		}

		if (change(Skid, false)) {
			WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = 0;
		} else {
			WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = (WBS_Node_WBS_BSCU_Command_Switch
					+ WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1);
		}

		if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
			Sys_Mode = 1;
		} else {
			Sys_Mode = 0;
		}

		if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
			WBS_Node_WBS_BSCU_Switch2 = 0;
		} else {
			if (change((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 0)
					&& (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 1), false)) {
				WBS_Node_WBS_BSCU_Switch2 = 0;
			} else {
				if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 1)
						&& (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 2))) {
					WBS_Node_WBS_BSCU_Switch2 = 1;
				} else {
					if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 2)
							&& (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 3))) {
						WBS_Node_WBS_BSCU_Switch2 = 2;
					} else {
						if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 3)
								&& (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 4))) {
							WBS_Node_WBS_BSCU_Switch2 = 3;
						} else {
							WBS_Node_WBS_BSCU_Switch2 = 4;
						}
					}
				}
			}
		}

		if (change(WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1,WBS_Node_WBS_Green_Pump_IsolationValve_Switch == 1)) {
			WBS_Node_WBS_SelectorValve_Switch = WBS_Node_WBS_Green_Pump_IsolationValve_Switch;
		} else {
			WBS_Node_WBS_SelectorValve_Switch = 0;
		}

		if ((WBS_Node_WBS_BSCU_Switch2 == 0)) {
			Nor_Pressure = 0;
		} else {
			if ((WBS_Node_WBS_BSCU_Switch2 == 1)) {
				Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 4);
			} else {
				if ((WBS_Node_WBS_BSCU_Switch2 == 2)) {
					Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 2);
				} else {
					if ((WBS_Node_WBS_BSCU_Switch2 == 3)) {
						Nor_Pressure = ((WBS_Node_WBS_SelectorValve_Switch / 4) * 3);
					} else {
						if ((WBS_Node_WBS_BSCU_Switch2 == 4)) {
							Nor_Pressure = WBS_Node_WBS_SelectorValve_Switch;
						} else {
							Nor_Pressure = 0;
						}
					}
				}
			}
		}

		if (change(WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 0, WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 <= 0)) {
			Alt_Pressure = 0;
		} else {
			if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 1)) {
				Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 4);
			} else {
				if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 2)) {
					Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 2);
				} else {
					if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 3)) {
						Alt_Pressure = ((WBS_Node_WBS_AS_MeterValve_Switch / 4) * 3);
					} else {
						if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 4)) {
							Alt_Pressure = WBS_Node_WBS_AS_MeterValve_Switch;
						} else {
							Alt_Pressure = 0;
						}
					}
				}
			}
		}

		WBS_Node_WBS_rlt_PRE2 = Nor_Pressure;

		WBS_Node_WBS_BSCU_rlt_PRE1 = WBS_Node_WBS_BSCU_Switch2;

		WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = Sys_Mode;

	}

	public static void main(String[] args) {
		(new WBS_1c()).update(0, true, true);
	}
}
