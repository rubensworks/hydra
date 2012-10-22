//
//  RestoMenuView.m
//  Hydra
//
//  Created by Yasser Deceukelier on 22/07/12.
//  Copyright (c) 2012 Zeus WPI. All rights reserved.
//

#import "RestoMenuView.h"
#import "NSDate+Utilities.h"

@interface RestoMenuView ()

// TODO: vormen dit geen retain-cycles?
@property (nonatomic, strong) UILabel *dateHeader;
@property (nonatomic, strong) UIView *soupHeader;
@property (nonatomic, strong) UIView *meatHeader;
@property (nonatomic, strong) UIView *vegetableHeader;

@property (nonatomic, strong) UIImageView *closedView;
@property (nonatomic, strong) UIActivityIndicatorView *spinner;

@end

@implementation RestoMenuView

#pragma mark - Constants

#define kDateHeaderHeight 45
#define kSectionHeaderHeight 40
#define kSectionFooterHeight 20
#define kRowHeight 22

#pragma mark - Properties and init

- (id)initWithRestoMenu:(id)menu andDate:(NSDate *)day inFrame:(CGRect)frame
{
    self = [super initWithFrame:frame style:UITableViewStylePlain];
    if (self) {
        self.dataSource = self;
        self.delegate = self;
        self.menu = menu;
        self.day = day;
        
        [self loadView];
    }
    return self;
}

- (void)setDay:(NSDate *)day
{
    if(day != _day) {
        _day = day;
        [self reloadData];
    }
}

- (void)setMenu:(id)menu
{    
    if (menu == [NSNull null]) menu = nil;
    if(menu != _menu) {
    	_menu = menu;
        [self reloadData];
    }
}

- (void)loadView
{
    self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    self.spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.spinner.center = self.center;
    
    CGRect closedFrame = CGRectMake(0, kDateHeaderHeight, self.bounds.size.width, 
                                    self.bounds.size.height - 2*kDateHeaderHeight);
    self.closedView = [[UIImageView alloc] initWithFrame:closedFrame];
    self.closedView.image = [UIImage imageNamed:@"resto-closed.jpg"];
    self.closedView.contentMode = UIViewContentModeCenter;

    CGRect headerFrame = CGRectMake(0, 0, self.bounds.size.width, kDateHeaderHeight);
    UIImageView *header = [[UIImageView alloc] initWithFrame:headerFrame];
    header.contentMode = UIViewContentModeScaleToFill;
    header.image = [UIImage imageNamed:@"header-bg"];
    self.tableHeaderView = header;

    CGRect dateHeaderFrame = CGRectMake(0, 3, headerFrame.size.width, headerFrame.size.height - 3);
    self.dateHeader = [[UILabel alloc] initWithFrame:dateHeaderFrame];
    self.dateHeader.font = [UIFont boldSystemFontOfSize:19];
    self.dateHeader.textAlignment = UITextAlignmentCenter;
    self.dateHeader.textColor = [UIColor whiteColor];
    self.dateHeader.backgroundColor = [UIColor clearColor];
    self.dateHeader.shadowColor = [UIColor blackColor];
    self.dateHeader.shadowOffset = CGSizeMake(0, 2);
    [header addSubview:self.dateHeader];
    
    self.bounces = NO;
    self.rowHeight = kRowHeight;
    self.separatorColor = [UIColor clearColor];
    self.allowsSelection = NO;
    [self reloadData];
}

- (void)reloadData
{    
    NSString *dateString;
    if ([self.day isToday]) dateString = @"Vandaag";
    else if ([self.day isTomorrow]) dateString = @"Morgen";
    else {
        // Create capitalized, formatted string
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"EEEE d MMMM"];
        dateString = [formatter stringFromDate:self.day];
        dateString = [dateString stringByReplacingCharactersInRange:NSMakeRange(0, 1)
                      withString:[[dateString substringToIndex:1] capitalizedString]];
    }
    [self.dateHeader setText:dateString];
    
    if (!self.menu) {
        [self.closedView removeFromSuperview];
        [self addSubview:self.spinner];
        [self.spinner startAnimating];
    }
    else {
        [self.spinner removeFromSuperview];
        [self.spinner stopAnimating];

        if (!self.menu.open) {
            [self addSubview:self.closedView];
        } 
    }
    [super reloadData];
}

#pragma mark - Table view datasource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return (self.menu.open ? 3 : 0);
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    if(section == 0) {
        return 1;
    } else if (section == 1) {
        return [self.menu.meat count];
    } else { //section == 2
        return [self.menu.vegetables count];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"RestoMenuViewCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if(!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 
                                      reuseIdentifier:cellIdentifier];
        cell.textLabel.textColor = [UIColor blackColor];
        cell.detailTextLabel.textColor = cell.textLabel.textColor;
    }

    if(indexPath.section == 0) {
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.detailTextLabel.font = cell.textLabel.font;
        cell.textLabel.text = self.menu.soup.name;
        cell.detailTextLabel.text = self.menu.soup.price;
    }
    else if (indexPath.section == 1) {
        RestoMenuItem *item = [self.menu.meat objectAtIndex:indexPath.row];
        
        if(item.recommended) {
            cell.textLabel.font = [UIFont boldSystemFontOfSize:15];
        } else {
            cell.textLabel.font = [UIFont systemFontOfSize:15];
        }
        cell.detailTextLabel.font = cell.textLabel.font;
        
        cell.textLabel.text = item.name;
        cell.detailTextLabel.text = item.price;
    }
    else { // section == 2
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.text = [self.menu.vegetables objectAtIndex:indexPath.row];
    }
    
    return cell;
}

#pragma mark - Table view delegate 

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return kSectionHeaderHeight;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    
    if(section == 0) {
        if(!self.soupHeader) {
            UIImage *soupImage = [[UIImage alloc] initWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"icon-soup" ofType:@"png"]];
            self.soupHeader = [self headerWithImage:soupImage andTitle:@"Soep"];
        }
        return self.soupHeader;
    }
    else if(section == 1) {
        if(!self.meatHeader) {
            UIImage *meatImage = [[UIImage alloc] initWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"icon-meal" ofType:@"png"]];
            self.meatHeader = [self headerWithImage:meatImage andTitle:@"Vlees en veggie"];
        }
        return self.meatHeader;
    }
    else { //section == 2
        if(!self.vegetableHeader) {
            UIImage *vegetableImage = [[UIImage alloc] initWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"icon-vegetables" ofType:@"png"]];
            self.vegetableHeader = [self headerWithImage:vegetableImage andTitle:@"Groenten"];
        }
        return self.vegetableHeader;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    
    return (section < 2 ? kSectionFooterHeight : 0);
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    UILabel *tildeLabel = nil;
    if(section < 2) {
        tildeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        tildeLabel.font = [UIFont fontWithName:@"Baskerville-Italic" size:24];
        tildeLabel.textAlignment = UITextAlignmentCenter;
        tildeLabel.text = @"~";
        tildeLabel.textColor = [UIColor lightGrayColor];
    }
    return tildeLabel;
}

 
#pragma mark - Utility methods

- (UIView *)headerWithImage:(UIImage *)image andTitle:(NSString *)title {
    UIFont *font = [UIFont fontWithName:@"Baskerville-SemiBold" size:20];

    CGSize textSize = [title sizeWithFont:font];
    NSUInteger padding = (self.bounds.size.width -textSize.width)/2;
    
    CGRect headerFrame = CGRectMake(0, 0, self.bounds.size.width, kSectionHeaderHeight);
    UIView *header = [[UIView alloc] initWithFrame:headerFrame];

    CGRect iconFrame = CGRectMake(padding -kSectionHeaderHeight, 5, kSectionHeaderHeight - 10, kSectionHeaderHeight - 10);
    UIImageView *iconView = [[UIImageView alloc] initWithFrame:iconFrame];
    iconView.image = image;
    [header addSubview:iconView];
    
    CGRect titleFrame = CGRectMake(padding, 0,
                                   textSize.width, kSectionHeaderHeight);
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:titleFrame];
    titleLabel.textAlignment = UITextAlignmentCenter;
    titleLabel.baselineAdjustment = UIBaselineAdjustmentAlignCenters;
    titleLabel.font = font;
    titleLabel.text = title;
    [header addSubview:titleLabel];
    
    return header;
}

@end
