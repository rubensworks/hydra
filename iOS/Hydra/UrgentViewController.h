//
//  UrgentViewController.h
//  Hydra
//
//  Created by Pieter De Baets on 05/12/12.
//  Copyright (c) 2012 Zeus WPI. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UrgentViewController : UIViewController

@property (nonatomic, unsafe_unretained) IBOutlet UIButton *playButton;
@property (nonatomic, unsafe_unretained) IBOutlet UILabel *songLabel;
@property (nonatomic, unsafe_unretained) IBOutlet UILabel *previousSongLabel;
@property (nonatomic, unsafe_unretained) IBOutlet UIView *songWrapper;
@property (nonatomic, unsafe_unretained) IBOutlet UIView *previousSongWrapper;

- (IBAction)playButtonTapped:(id)sender;
- (IBAction)homeButtonTapped:(id)sender;
- (IBAction)facebookButtonTapped:(id)sender;
- (IBAction)twitterButtonTapped:(id)sender;
- (IBAction)soundcloudButtonTapped:(id)sender;
- (IBAction)mailButtonTapped:(id)sender;

@end