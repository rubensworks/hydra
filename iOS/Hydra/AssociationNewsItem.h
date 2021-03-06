//
//  AssociationNewsItem.h
//  Hydra
//
//  Created by Pieter De Baets on 21/07/12.
//  Copyright (c) 2012 Zeus WPI. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RKObjectMapping, Association;

@interface AssociationNewsItem : NSObject <NSCoding>

@property (nonatomic, strong) NSString *title;
@property (nonatomic, strong) Association *association;
@property (nonatomic, strong) NSDate *date;
@property (nonatomic, strong) NSString *content;
@property (nonatomic, assign) BOOL highlighted;

+ (RKObjectMapping *)objectMapping;

@end
