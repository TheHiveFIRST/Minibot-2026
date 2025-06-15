// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private final DifferentialDrive m_robotDrive;
  private final Timer timer;

  private final XboxController m_controller = new XboxController(0);

  private final WPI_TalonSRX leftFront = new WPI_TalonSRX(1);
  private final WPI_TalonSRX rightFront = new WPI_TalonSRX(2);
  private final WPI_TalonSRX leftBack = new WPI_TalonSRX(3);
  private final WPI_TalonSRX rightBack = new WPI_TalonSRX(4);

  /** Called once at the beginning of the robot program. */
  public Robot() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.

    rightBack.follow(rightFront);
    leftBack.follow(leftFront);

    timer = new Timer();
    m_robotDrive = new DifferentialDrive(leftFront::set, rightFront::set);
  }

  @Override
  public void autonomousInit() {
    timer.reset();
    timer.start();
  }

  @Override
  public void autonomousPeriodic() {
    if (timer.get() < 2.0) {
      leftFront.set(0.3);
      rightFront.set(0.3);
    } else {
      leftFront.set(0);
      rightFront.set(0);
    }
  }

  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(-m_controller.getLeftY(), m_controller.getRightY());
  }
}
