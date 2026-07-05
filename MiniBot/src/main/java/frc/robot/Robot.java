// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the DifferentialDrive class,
 * specifically it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private final DifferentialDrive m_robotDrive;
  private final Timer timer;

  private final XboxController m_controller = new XboxController(0);

  private final WPI_TalonSRX leftFront = new WPI_TalonSRX(1);
  private final WPI_TalonSRX rightFront = new WPI_TalonSRX(2);
  private final WPI_TalonSRX leftBack = new WPI_TalonSRX(3);
  private final WPI_TalonSRX rightBack = new WPI_TalonSRX(4);

  // Double solenoid on PCM ports 1 (forward) and 2 (reverse)
  private final DoubleSolenoid m_solenoid =
      new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 1, 2);

  // Tracks current toggle state so we know which way to flip next
  private boolean m_solenoidExtended = false;

  /** Called once at the beginning of the robot program. */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.

    rightBack.follow(rightFront);
    leftBack.follow(leftFront);

    timer = new Timer();
    m_robotDrive = new DifferentialDrive(leftFront::set, rightFront::set);

    rightFront.setInverted(true);
    rightBack.setInverted(true);

    // Start the solenoid in a known state (retracted)
    m_solenoid.set(DoubleSolenoid.Value.kReverse);
  }

  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    if (timer.get() < 5.0) {
      // Smaller speed difference = wider turn = larger circle
      leftFront.set(0.15);   // Original was 0.3 * 0.5 = 0.15
      rightFront.set(0.15);  // Original was 0.3 * 0.5 = 0.15
      m_solenoid.set(DoubleSolenoid.Value.kForward);
    } else {
      leftFront.set(0);
      rightFront.set(0);
      m_solenoid.set(DoubleSolenoid.Value.kReverse);
    }
  }

  @Override
  public void teleopPeriodic() {
    m_robotDrive.tankDrive(-m_controller.getLeftY() * 0.5, -m_controller.getRightY() * 0.5);  // tank drive mode

    // Toggle the pneumatic in/out each time the A button is pressed
    if (m_controller.getAButtonPressed()) {
      m_solenoidExtended = !m_solenoidExtended;
      m_solenoid.set(m_solenoidExtended ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    }
  }
}